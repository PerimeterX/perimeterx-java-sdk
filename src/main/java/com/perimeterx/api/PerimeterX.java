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

import com.perimeterx.api.activities.ActivityHandler;
import com.perimeterx.api.activities.BufferedActivityHandler;
import com.perimeterx.api.blockhandler.BlockHandler;
import com.perimeterx.api.blockhandler.CaptchaBlockHandler;
import com.perimeterx.api.blockhandler.DefaultBlockHandler;
import com.perimeterx.api.providers.DefaultHostnameProvider;
import com.perimeterx.api.providers.HostnameProvider;
import com.perimeterx.api.providers.IPProvider;
import com.perimeterx.api.providers.RemoteAddressIPProvider;
import com.perimeterx.api.verificationhandler.DefaultVerificationHandler;
import com.perimeterx.api.verificationhandler.VerificationHandler;
import com.perimeterx.http.PXHttpClient;
import com.perimeterx.internals.PXCaptchaValidator;
import com.perimeterx.internals.PXCookieValidator;
import com.perimeterx.internals.PXS2SValidator;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.exceptions.PXException;
import com.perimeterx.models.risk.PassReason;
import com.perimeterx.utils.Constants;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * Facade object for - configuring, validating and blocking requests
 * <p>
 * Created by shikloshi on 03/07/2016.
 */
public class PerimeterX {

    private Logger logger = LoggerFactory.getLogger(PerimeterX.class);

    private static PerimeterX instance = null;

    private PXConfiguration configuration;
    private BlockHandler blockHandler;
    private PXS2SValidator serverValidator;
    private PXCookieValidator cookieValidator;
    private ActivityHandler activityHandler;
    private PXCaptchaValidator captchaValidator;
    private IPProvider ipProvider = new RemoteAddressIPProvider();
    private HostnameProvider hostnameProvider = new DefaultHostnameProvider();
    private VerificationHandler verificationHandler;

    /**
     * Build a singleton object from configuration
     *
     * @param configuration - {@link PXConfiguration}
     * @return PerimeterX object
     * @deprecated use public constructor instead
     */
    @Deprecated
    public static PerimeterX getInstance(PXConfiguration configuration) throws PXException {
        if (instance == null) {
            synchronized (PerimeterX.class) {
                if (instance == null) {
                    instance = new PerimeterX(configuration);
                }
            }
        }
        return instance;
    }

    private CloseableHttpClient getHttpClient(int timeout) {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(200);
        cm.setDefaultMaxPerRoute(20);
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
                .setSocketTimeout(timeout)
                .build();
        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .setDefaultRequestConfig(config)
                .build();
        return httpClient;
    }

    private CloseableHttpAsyncClient getAsyncHttpClient() {
        return HttpAsyncClients.createDefault();
    }

    private void init(PXConfiguration configuration) throws PXException {
        this.configuration = configuration;
        PXHttpClient pxClient = PXHttpClient.getInstance(configuration, getAsyncHttpClient(), getHttpClient(this.configuration.getApiTimeout()));
        if (this.configuration.isCaptchaEnabled()) {
            this.blockHandler = new CaptchaBlockHandler();
        } else {
            this.blockHandler = new DefaultBlockHandler();
        }
        this.serverValidator = new PXS2SValidator(pxClient, this.configuration);
        this.captchaValidator = new PXCaptchaValidator(pxClient);
        this.activityHandler = new BufferedActivityHandler(pxClient, this.configuration);
        this.cookieValidator = PXCookieValidator.getDecoder(this.configuration.getCookieKey());
        this.verificationHandler = new DefaultVerificationHandler(this.configuration, this.activityHandler, this.blockHandler);
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
     * @return true if request is valid
     * @throws PXException - PXException
     */
    public boolean pxVerify(HttpServletRequest req, HttpServletResponseWrapper responseWrapper) throws PXException {
        PXContext context = null;
        try {
            if (!moduleEnabled()) {
                logger.info("PerimeterX verification SDK is disabled");
                return true;
            }
            // Remove captcha cookie to prevent re-use
            Cookie cookie = new Cookie(Constants.COOKIE_CAPTCHA_KEY, StringUtils.EMPTY);
            cookie.setMaxAge(0);
            responseWrapper.addCookie(cookie);

            context = new PXContext(req, this.ipProvider, this.hostnameProvider, configuration);
            if (captchaValidator.verify(context)) {
                return verificationHandler.handleVerification(context, responseWrapper);
            }

            boolean cookieVerified = cookieValidator.verify(this.configuration ,context);
            // Cookie is valid (exists and not expired) so we can block according to it's score
            if (cookieVerified) {
                logger.info("No risk API Call is needed, using cookie");
                return verificationHandler.handleVerification(context, responseWrapper);
            }

            // Calls risk_api and populate the data retrieved to the context
            serverValidator.verify(context);

            return verificationHandler.handleVerification(context,responseWrapper);
        } catch (Exception e) {
            logger.error("Unexpected error: {} - request passed", e.getMessage());
            // If any general exception is being thrown, notify in page_request activity
            if (context != null){
                context.setPassReason(PassReason.ERROR);
                activityHandler.handlePageRequestedActivity(context);
            }
            return true;
        }
    }

    private boolean moduleEnabled() {
        return this.configuration.isModuleEnabled();
    }

    /**
     * Set activity handler
     *
     * @param activityHandler - new activity handler to use
     */
    public void setActivityHandler(ActivityHandler activityHandler) {
        this.activityHandler = activityHandler;
    }

    /**
     * Set block handler
     *
     * @param blockHandler - new block handler to use
     */
    public void setBlockHandler(BlockHandler blockHandler) {
        this.blockHandler = blockHandler;
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
    public void setVerificationHandler(VerificationHandler verificationHandler){
        this.verificationHandler = verificationHandler;
    }
}