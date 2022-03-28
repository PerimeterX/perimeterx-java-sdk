package com.perimeterx.api.verificationhandler;

import com.perimeterx.api.activities.ActivityHandler;
import com.perimeterx.api.blockhandler.BlockHandler;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.configuration.ModuleMode;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.exceptions.PXException;
import com.perimeterx.utils.PXLogger;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponseWrapper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.perimeterx.utils.PXLogger.LogReason.DEBUG_S2S_SCORE_IS_HIGHER_THAN_BLOCK;
import static com.perimeterx.utils.PXLogger.LogReason.DEBUG_S2S_SCORE_IS_LOWER_THAN_BLOCK;

/**
 * Created by nitzangoldfeder on 28/05/2017.
 */
public class DefaultVerificationHandler implements VerificationHandler {

    private static final PXLogger logger = PXLogger.getLogger(DefaultVerificationHandler.class);

    private PXConfiguration pxConfiguration;
    private ActivityHandler activityHandler;
    private BlockHandler blockHandler;

    public DefaultVerificationHandler(PXConfiguration pxConfiguration, ActivityHandler activityHandler) {
        this.pxConfiguration = pxConfiguration;
        this.activityHandler = activityHandler;
        this.blockHandler = pxConfiguration.getBlockHandler();
    }

    @Override
    public boolean handleVerification(PXContext context, HttpServletResponseWrapper responseWrapper) throws PXException {
        boolean verified = shouldPassRequest(context);
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
        setPxhdCookie(context, responseWrapper);
        boolean shouldBypassMonitor = shouldBypassMonitor(context);
        if (!verified && (context.isBlocking() || shouldBypassMonitor)) {
            this.blockHandler.handleBlocking(context, this.pxConfiguration, responseWrapper);
            return false;
        }

        return true;
    }

    private boolean shouldBypassMonitor(PXContext context) {
        String bypassHeader = this.pxConfiguration.getBypassMonitorHeader();
        return !StringUtils.isEmpty(bypassHeader) && context.getHeaders().containsKey(bypassHeader.toLowerCase())
                && context.getHeaders().get(bypassHeader.toLowerCase()).equals("1");
    }

    private void setPxhdCookie(PXContext context, HttpServletResponseWrapper responseWrapper) {
        try {
            if (!StringUtils.isEmpty(context.getResponsePxhd()) && !context.getResponsePxhd().equals(context.getPxhd())) {
                String pxhdCookieValue = context.getResponsePxhd();
                Cookie cookie = new Cookie("_pxhd", URLEncoder.encode(pxhdCookieValue, "UTF-8"));
                cookie.setPath("/");
                cookie.setMaxAge(3600 * 24 * 365);
                responseWrapper.addCookie(cookie);
            }
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage());
        }
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