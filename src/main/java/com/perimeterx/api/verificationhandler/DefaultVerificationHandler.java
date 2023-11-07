package com.perimeterx.api.verificationhandler;

import com.perimeterx.api.activities.ActivityHandler;
import com.perimeterx.api.blockhandler.BlockHandler;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.exceptions.PXException;
import com.perimeterx.utils.PXLogger;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletResponseWrapper;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static com.perimeterx.utils.PXLogger.LogReason.DEBUG_S2S_SCORE_IS_HIGHER_THAN_BLOCK;
import static com.perimeterx.utils.PXLogger.LogReason.DEBUG_S2S_SCORE_IS_LOWER_THAN_BLOCK;
import static org.apache.commons.lang3.StringUtils.isNoneBlank;

/**
 * Created by nitzangoldfeder on 28/05/2017.
 */
public class DefaultVerificationHandler implements VerificationHandler {
    final String SET_COOKIE_KEY_HEADER = "Set-Cookie";
    final String PXHD_COOKIE_KEY = "_pxhd=";
    final String COOKIE_SEPARATOR = ";";
    final int  ONE_YEAR_IN_SECONDS = 3600 * 24 * 365;
    final String COOKIE_MAX_AGE = "Max-Age=" + ONE_YEAR_IN_SECONDS + COOKIE_SEPARATOR;
    final String PXHD_COOKIE_PATH = "Path=/";
    final String COOKIE_SAME_SITE = "SameSite=Lax" + COOKIE_SEPARATOR;
    final String COOKIE_DOMAIN_KEY = "Domain=";

    private static final PXLogger logger = PXLogger.getLogger(DefaultVerificationHandler.class);

    private final PXConfiguration pxConfiguration;
    private final ActivityHandler activityHandler;
    private final BlockHandler blockHandler;

    public DefaultVerificationHandler(PXConfiguration pxConfiguration, ActivityHandler activityHandler) {
        this.pxConfiguration = pxConfiguration;
        this.activityHandler = activityHandler;
        this.blockHandler = pxConfiguration.getBlockHandler();
    }

    @Override
    public boolean handleVerification(PXContext context, HttpServletResponseWrapper responseWrapper) throws PXException {
        boolean verified = shouldPassRequest(context);

        setPxhdCookie(context, responseWrapper);

        if (!verified && !context.isMonitoredRequest()) {
            this.blockHandler.handleBlocking(context, this.pxConfiguration, responseWrapper);
        }

        try {
            if (verified) {
                logger.debug("Passing request {} {}", verified, this.pxConfiguration.getModuleMode());

                // Not blocking request and sending page_requested activity to px if configured as true
                if (this.pxConfiguration.isSendPageActivities()) {
                    this.activityHandler.handlePageRequestedActivity(context);
                }
            } else {
                logger.debug("Request invalid");
                this.activityHandler.handleBlockActivity(context);
            }
        } catch (PXException pxException) {
            logger.error("Error occurred while handle activities", pxException);
        }

        return verified || context.isMonitoredRequest();
    }

    private void setPxhdCookie(PXContext context, HttpServletResponseWrapper responseWrapper) {
        try {
            if (!StringUtils.isEmpty(context.getResponsePxhd())) {
                final String cookieValue = getCookieValue(context);

                responseWrapper.addHeader(SET_COOKIE_KEY_HEADER, cookieValue);
            }
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage());
        }
    }

    private String getCookieValue(PXContext context) throws UnsupportedEncodingException {
        final String pxHDCookieValue = context.getResponsePxhd();
        final String pxHDEntry = PXHD_COOKIE_KEY + URLEncoder.encode(pxHDCookieValue, StandardCharsets.UTF_8.name()) + COOKIE_SEPARATOR;

        String cookieValue =  pxHDEntry
                + COOKIE_MAX_AGE
                + COOKIE_SAME_SITE
                + PXHD_COOKIE_PATH;

        if (isNoneBlank(context.getPxhdDomain())) {
            cookieValue += COOKIE_SEPARATOR + COOKIE_DOMAIN_KEY + context.getPxhdDomain();
        }

        return cookieValue;
    }

    private boolean shouldPassRequest(PXContext context) {
        int score = context.getRiskScore();
        int blockingScore = this.pxConfiguration.getBlockingScore();
        // If should block this request we will apply our block handle and send the block activity to px

        boolean verified = score < blockingScore;
        logger.debug(verified ? DEBUG_S2S_SCORE_IS_LOWER_THAN_BLOCK : DEBUG_S2S_SCORE_IS_HIGHER_THAN_BLOCK, score, blockingScore);

        return verified;
    }
}