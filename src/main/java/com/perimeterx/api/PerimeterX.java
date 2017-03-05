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
import com.perimeterx.api.activities.DefaultActivityHandler;
import com.perimeterx.api.blockhandler.BlockHandler;
import com.perimeterx.api.blockhandler.CaptchaBlockHandler;
import com.perimeterx.api.blockhandler.DefaultBlockHandler;
import com.perimeterx.api.providers.DefaultHostnameProvider;
import com.perimeterx.api.providers.HostnameProvider;
import com.perimeterx.api.providers.IPProvider;
import com.perimeterx.api.providers.RemoteAddressIPProvider;
import com.perimeterx.http.PXHttpClient;
import com.perimeterx.internals.PXCaptchaValidator;
import com.perimeterx.internals.PXCookieValidator;
import com.perimeterx.internals.PXS2SValidator;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.exceptions.PXException;
import com.perimeterx.models.httpmodels.RiskRequest;
import com.perimeterx.models.httpmodels.RiskResponse;
import com.perimeterx.models.risk.BlockReason;
import com.perimeterx.models.risk.S2SCallReason;
import com.perimeterx.utils.Constants;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
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

    Logger logger = LoggerFactory.getLogger(PerimeterX.class);

    private static PerimeterX instance = null;

    private PXConfiguration configuration;
    private BlockHandler blockHandler;
    private PXS2SValidator serverValidator;
    private PXCookieValidator cookieValidator;
    private ActivityHandler activityHandler;
    private PXCaptchaValidator captchaValidator;
    private IPProvider ipProvider = new RemoteAddressIPProvider();
    private HostnameProvider hostnameProvider = new DefaultHostnameProvider();

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
                .setConnectionRequestTimeout(timeout)
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
        this.serverValidator = new PXS2SValidator(pxClient);
        this.captchaValidator = new PXCaptchaValidator(pxClient);
        this.activityHandler = new BufferedActivityHandler(pxClient, this.configuration);
        this.cookieValidator = PXCookieValidator.getDecoder(this.configuration.getCookieKey());
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
        try {
            if (!moduleEnabled()) {
                logger.info("PerimeterX verification SDK is disabled");
                return true;
            }
            // Remove captcha cookie to prevent re-use
            Cookie cookie = new Cookie(Constants.COOKIE_CAPTCHA_KEY, StringUtils.EMPTY);
            cookie.setMaxAge(0);
            responseWrapper.addCookie(cookie);

            PXContext context = new PXContext(req, this.ipProvider, this.hostnameProvider, configuration.getAppId());
            if (captchaValidator.verify(context)) {
                return handleVerification(context, responseWrapper, BlockReason.COOKIE);
            }
            S2SCallReason callReason = cookieValidator.verify(context);
            logger.info("Risk API call reason: {}", callReason);
            // Cookie is valid (exists and not expired) so we can block according to it's score
            if (callReason == S2SCallReason.NONE) {
                logger.info("No risk API Call is needed, using cookie");
                return handleVerification(context, responseWrapper, BlockReason.COOKIE);
            }

            context.setS2sCallReason(callReason);
            // Calls risk_api and populate the data retrieved to the context
            RiskRequest request = RiskRequest.fromContext(context);
            RiskResponse response = serverValidator.verify(request);
            if (response != null) {
                context.setScore(response.getScores().getNonHuman());
                context.setUuid(response.getUuid());
                return handleVerification(context, responseWrapper, BlockReason.SERVER);
            }
            return true;
        } catch (Exception e) {
            logger.error("Unexpected error: {} - request passed", e.getMessage());
            return true;
        }
    }

    private boolean handleVerification(PXContext context, HttpServletResponseWrapper responseWrapper, BlockReason blockReason) throws PXException {
        int score = context.getScore();
        int blockingScore = this.configuration.getBlockingScore();
        // If should block this request we will apply our block handle and send the block activity to px
        boolean verified = score < blockingScore;
        logger.info("Request score: {}, Blocking score: {}", score, blockingScore);
        if (verified) {
            logger.info("Request valid");
            // Not blocking request and sending page_requested activity to px if configured as true
            if (this.configuration.shouldSendPageActivities()) {
                this.activityHandler.handlePageRequestedActivity(context);
            }
        } else {
            logger.info("Request invalid");
            context.setBlockReason(blockReason);
            this.activityHandler.handleBlockActivity(context);
            this.blockHandler.handleBlocking(context, responseWrapper);
        }
        return verified;
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
}
