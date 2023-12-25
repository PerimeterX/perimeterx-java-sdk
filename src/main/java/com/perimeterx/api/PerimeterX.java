/**
 * Copyright © 2016 PerimeterX, Inc.
 * * Permission is hereby granted, free of charge, to any
 * * person obtaining a copy of this software and associated
 * * documentation files (the "Software"), to deal in the
 * * Software without restriction, including without limitation
 * * the rights to use, copy, modify, merge, publish,
 * * distribute, sublicense, and/or sell copies of the
 * * Software, and to permit persons to whom the Software is
 * * furnished to do so, subject to the following conditions:
 * *
 * * The above copyright notice and this permission notice
 * * shall be included in all copies or substantial portions of
 * * the Software.
 * *
 * * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * * KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * * PURPOSE AND NONINFINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.perimeterx.api;

import com.google.gson.Gson;
import com.perimeterx.api.activities.ActivityHandler;
import com.perimeterx.api.activities.BufferedActivityHandler;
import com.perimeterx.api.additionalContext.credentialsIntelligence.loginresponse.LoginResponseValidator;
import com.perimeterx.api.additionalContext.credentialsIntelligence.loginresponse.LoginResponseValidatorFactory;
import com.perimeterx.api.providers.CombinedIPProvider;
import com.perimeterx.api.providers.DefaultHostnameProvider;
import com.perimeterx.api.providers.HostnameProvider;
import com.perimeterx.api.providers.IPProvider;
import com.perimeterx.api.proxy.ReverseProxy;
import com.perimeterx.api.remoteconfigurations.DefaultRemoteConfigManager;
import com.perimeterx.api.remoteconfigurations.RemoteConfigurationManager;
import com.perimeterx.api.remoteconfigurations.TimerConfigUpdater;
import com.perimeterx.api.verificationhandler.DefaultVerificationHandler;
import com.perimeterx.api.verificationhandler.TestVerificationHandler;
import com.perimeterx.api.verificationhandler.VerificationHandler;
import com.perimeterx.http.PXClient;
import com.perimeterx.http.RequestWrapper;
import com.perimeterx.http.ResponseWrapper;
import com.perimeterx.internals.PXCookieValidator;
import com.perimeterx.internals.PXS2SValidator;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.activities.Activity;
import com.perimeterx.models.activities.UpdateReason;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.configuration.PXDynamicConfiguration;
import com.perimeterx.models.exceptions.PXException;
import com.perimeterx.utils.EnforcerErrorUtils;
import com.perimeterx.utils.HMACUtils;
import com.perimeterx.utils.logger.LogReason;
import com.perimeterx.utils.logger.IPXLogger;
import com.perimeterx.utils.StringUtils;
import com.perimeterx.utils.logger.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.Closeable;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import static com.perimeterx.utils.Constants.*;
import static java.util.Objects.isNull;

/**
 * Facade object for - configuring, validating and blocking requests
 * <p>
 * Created by shikloshi on 03/07/2016.
 */

public class PerimeterX implements Closeable {

    public static IPXLogger globalLogger;
    private PXConfiguration configuration;
    private PXS2SValidator serverValidator;
    private PXCookieValidator cookieValidator;
    private ActivityHandler activityHandler;
    private IPProvider ipProvider;
    private HostnameProvider hostnameProvider;
    private VerificationHandler verificationHandler;
    private ReverseProxy reverseProxy;
    private PXClient pxClient = null;
    private RequestFilter requestFilter;

    private void init(PXConfiguration configuration) throws PXException {
        globalLogger = configuration.getLoggerFactory().getLogger();
        globalLogger.debug(LogReason.DEBUG_INITIALIZING_MODULE);
        configuration.mergeConfigurations();
        this.configuration = configuration;
        hostnameProvider = new DefaultHostnameProvider();
        ipProvider = new CombinedIPProvider(configuration);
        setPxClient(configuration);
        this.activityHandler = new BufferedActivityHandler(pxClient, this.configuration);
        this.requestFilter = new RequestFilter(configuration);

        if (configuration.isRemoteConfigurationEnabled()) {
            RemoteConfigurationManager remoteConfigManager = new DefaultRemoteConfigManager(configuration, pxClient);
            PXDynamicConfiguration initialConfig = remoteConfigManager.getConfiguration();
            if (initialConfig == null) {
                remoteConfigManager.disableModuleOnError();
            } else {
                remoteConfigManager.updateConfiguration(initialConfig);
            }
            TimerConfigUpdater timerConfigUpdater = new TimerConfigUpdater(remoteConfigManager, configuration, activityHandler);
            timerConfigUpdater.schedule();
        }

        this.serverValidator = new PXS2SValidator(pxClient, this.configuration);
        this.cookieValidator = new PXCookieValidator(this.configuration);
        setVerificationHandler();
        setReverseProxy(configuration);
    }

    private void setReverseProxy(PXConfiguration configuration) {
        this.reverseProxy = configuration.getReverseProxyInstance();
    }

    private void setPxClient(PXConfiguration configuration) throws PXException {
        this.pxClient = configuration.getPxClientInstance();
    }

    private void setVerificationHandler() {
        if (this.configuration.isTestingMode()) {
            this.verificationHandler = new TestVerificationHandler(this.configuration, this.activityHandler);
        } else {
            this.verificationHandler = new DefaultVerificationHandler(this.configuration, this.activityHandler);
        }
    }

    public PerimeterX(PXConfiguration configuration) throws PXException {
        init(configuration);
    }

    public PerimeterX(PXConfiguration configuration, IPProvider ipProvider, HostnameProvider hostnameProvider) throws PXException {
        init(configuration);
        this.ipProvider = ipProvider;
        this.hostnameProvider = hostnameProvider;
    }

    public PerimeterX(PXConfiguration configuration, IPProvider ipProvider) throws PXException {
        init(configuration);
        this.ipProvider = ipProvider;
    }

    public PerimeterX(PXConfiguration configuration, HostnameProvider hostnameProvider) throws PXException {
        init(configuration);
        this.hostnameProvider = hostnameProvider;
    }

    /**
     * Verify http request using cookie or PX server call
     *
     * @param req             - current http call examined by PX
     * @param responseWrapper - response wrapper on which we will set the response according to PX verification.
     * @return PXContext, or null if module is disabled
     * @throws PXException - PXException
     */
    public PXContext pxVerify(HttpServletRequest req, HttpServletResponseWrapper responseWrapper) throws PXException {
        PXContext context = null;
        globalLogger.debug(LogReason.DEBUG_STARTING_REQUEST_VERIFICATION);

        try {

            if (!moduleEnabled()) {
                globalLogger.debug(LogReason.DEBUG_MODULE_DISABLED);
                return null;
            }

            context = new PXContext(req, this.ipProvider, this.hostnameProvider, configuration);


            if (shouldReverseRequest(req, responseWrapper, context)) {
                context.setFirstPartyRequest(true);
                return context;
            }

            if (requestFilter.isFilteredRequest(req, context)) {
                return context;
            }

            if (isValidTelemetryRequest(req)) {
                activityHandler.handleEnforcerTelemetryActivity(this.configuration, UpdateReason.COMMAND, context);
                return context;
            }

            handleCookies(context);
            addCustomHeadersToRequest(req, context);

            boolean isRequestVerified = verificationHandler.handleVerification(context, responseWrapper);
            context.setVerified(isRequestVerified);
        } catch (Exception e) {
            // If any general exception is being thrown, notify in page_request activity
            if (context != null) {
                if (!context.getS2sErrorReasonInfo().isErrorSet()) {
                    EnforcerErrorUtils.handleEnforcerError(context, "Unexpected error", e);
                }

                activityHandler.handlePageRequestedActivity(context);
                context.setVerified(true);
            }
        }

        return context;
    }

    private boolean moduleEnabled() {
        return this.configuration.isModuleEnabled();
    }

    private boolean shouldReverseRequest(HttpServletRequest req, HttpServletResponseWrapper res, PXContext context) throws IOException, URISyntaxException {
        return reverseProxy.reversePxClient(req, res, context) || reverseProxy.reversePxXhr(req, res, context) || reverseProxy.reverseCaptcha(req, res, context);
    }

    private void handleCookies(PXContext context) {
        if (cookieValidator.verify(context)) {
            context.logger.debug(LogReason.DEBUG_COOKIE_EVALUATION_FINISHED, context.getRiskScore());
            // Cookie is valid (exists and not expired) so we can block according to it's score
            return;
        }
        context.logger.debug(LogReason.DEBUG_COOKIE_MISSING);
        if (serverValidator.verify(context)) {
            context.logger.debug(LogReason.DEBUG_COOKIE_VERSION_FOUND, context.getCookieVersion());
        }
    }

    private void addCustomHeadersToRequest(HttpServletRequest request, PXContext context) {
        if (context.getLoginData() != null && context.getLoginData().getLoginCredentials() != null) {
            setBreachedAccount(request, context);
            setAdditionalS2SActivityHeaders(request, context);
        }
    }

    private void setBreachedAccount(HttpServletRequest request, PXContext context) {
        if (configuration.isLoginCredentialsExtractionEnabled() && context.isBreachedAccount()) {
            ((RequestWrapper) request).addHeader(configuration.getPxCompromisedCredentialsHeader(), String.valueOf(context.getPxde().get(BREACHED_ACCOUNT_KEY_NAME)));
        }
    }

    private void setAdditionalS2SActivityHeaders(HttpServletRequest request, PXContext context) {
        if (configuration.isAdditionalS2SActivityHeaderEnabled()) {
            final Activity activity = ((BufferedActivityHandler) activityHandler).createAdditionalS2SActivity(context);

            final String stringifyActivity = new Gson().toJson(activity);
            final String urlHeader = configuration.getServerURL() + API_ACTIVITIES;

            ((RequestWrapper) request).addHeader(ADDITIONAL_ACTIVITY_HEADER, stringifyActivity);
            ((RequestWrapper) request).addHeader(ADDITIONAL_ACTIVITY_URL_HEADER, urlHeader);
        }
    }

    public void pxPostVerify(ResponseWrapper response, PXContext context) throws PXException {
        try {
            if (context != null){
                if (response != null && !configuration.isAdditionalS2SActivityHeaderEnabled() && context.isContainCredentialsIntelligence()) {
                    handleAdditionalS2SActivityWithCI(response, context);
                }
                context.logger.sendMemoryLogs(this.configuration, context);
            }
        } catch (Exception e) {
            context.logger.error("Failed to post verify response. Error :: ", e);
        }
    }

    private void handleAdditionalS2SActivityWithCI(ResponseWrapper response, PXContext context) throws PXException {
        final LoginResponseValidator loginResponseValidator = LoginResponseValidatorFactory.create(configuration, context);

        context.getLoginData().setLoginSuccessful(loginResponseValidator.isSuccessfulLogin(response));
        context.getLoginData().setResponseStatusCode(response.getStatus());

        activityHandler.handleAdditionalS2SActivity(context);
    }

    public boolean isValidTelemetryRequest(HttpServletRequest request) {
        final String telemetryHeader = request.getHeader(DEFAULT_TELEMETRY_REQUEST_HEADER_NAME);

        if (isNull(telemetryHeader)) {
            return false;
        }
        try {
            globalLogger.debug("Received command to send enforcer telemetry");

            final String decodedString = new String(Base64.getDecoder().decode(telemetryHeader));
            final String[] splitTimestampAndHmac = decodedString.split(":");

            if (splitTimestampAndHmac.length != 2) {
                return false;
            }

            final String timestamp = splitTimestampAndHmac[0];
            final String hmac = splitTimestampAndHmac[1];

            if (Long.parseLong(timestamp) < System.currentTimeMillis()) {
                globalLogger.error("Telemetry command has expired.");
                return false;
            }

            final byte[] hmacBytes = HMACUtils.HMACString(timestamp, configuration.getCookieKey());
            final String generatedHmac = StringUtils.byteArrayToHexString(hmacBytes).toLowerCase();

            if (!MessageDigest.isEqual(generatedHmac.getBytes(), hmac.getBytes())) {
                globalLogger.error("Telemetry validation failed - invalid hmac, original=" + hmac + ", generated=" + generatedHmac);
                return false;
            }
        } catch (NoSuchAlgorithmException | InvalidKeyException | IllegalArgumentException e) {
            globalLogger.error("Telemetry validation failed.");
            return false;
        }

        return true;
    }

    /**
     * Set activity handler
     *
     * @param activityHandler - new activity handler to use
     */
    public void setActivityHandler(ActivityHandler activityHandler) {
        this.activityHandler = activityHandler;
        setVerificationHandler();
    }

    /**
     * Set IP Provider
     *
     * @param ipProvider - IP provider that is used to extract ip from request
     */
    public void setIpProvider(IPProvider ipProvider) {
        this.ipProvider = ipProvider;
    }

    /**
     * Set Hostname Provider
     *
     * @param hostnameProvider - Used to extract hostname from request
     */
    public void setHostnameProvider(HostnameProvider hostnameProvider) {
        this.hostnameProvider = hostnameProvider;
    }

    /**
     * Set Set Verification Handler
     *
     * @param verificationHandler - sets the verification handler for user customization
     */
    public void setVerificationHandler(VerificationHandler verificationHandler) {
        this.verificationHandler = verificationHandler;
    }

    @Override
    public void close() throws IOException {
        if (this.pxClient != null) {
            this.pxClient.close();
        }
    }
}