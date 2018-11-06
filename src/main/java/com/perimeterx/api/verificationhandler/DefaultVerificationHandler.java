package com.perimeterx.api.verificationhandler;

import com.perimeterx.api.activities.ActivityHandler;
import com.perimeterx.api.blockhandler.BlockHandler;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.configuration.ModuleMode;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.exceptions.PXException;
import com.perimeterx.utils.PXLogger;

import javax.servlet.http.HttpServletResponseWrapper;
import java.util.ArrayList;
import java.util.List;

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
            if (this.pxConfiguration.shouldSendPageActivities()) {
                this.activityHandler.handlePageRequestedActivity(context);
            }
        } else {
            logger.debug("Request invalid");
            this.activityHandler.handleBlockActivity(context);
        }
        setVidPxhdCookie(context, responseWrapper);
        if (pxConfiguration.getModuleMode().equals(ModuleMode.BLOCKING) && !verified) {
            this.blockHandler.handleBlocking(context, this.pxConfiguration, responseWrapper);
            return false;
        }

        return true;
    }

    private void setVidPxhdCookie(PXContext context, HttpServletResponseWrapper responseWrapper) {
        String setCookie = responseWrapper.getHeader("Set-Cookie");
        String pxhdCookieValue = context.getPxhd();
        String pxvidCookieValue = context.getVid();
        List <String> cookies = new ArrayList<>();
        if (setCookie != null) {
            cookies.add(setCookie);
        }
        if (pxvidCookieValue != null) {
            cookies.add("_pxvid=" + pxvidCookieValue);
        }
        if (pxhdCookieValue != null) {
            cookies.add("_pxhd=" + pxhdCookieValue);
        }
        String vidPxhdCookies = org.apache.commons.lang3.StringUtils.join(cookies, ", ");
        responseWrapper.setHeader("Set-Cookie", vidPxhdCookies);
    }

    private boolean shouldPassRequest(PXContext context){
        int score = context.getRiskScore();
        int blockingScore = this.pxConfiguration.getBlockingScore();
        // If should block this request we will apply our block handle and send the block activity to px

        boolean verified = score < blockingScore;
        logger.debug(verified ? DEBUG_S2S_SCORE_IS_LOWER_THAN_BLOCK : DEBUG_S2S_SCORE_IS_HIGHER_THAN_BLOCK, score, blockingScore);

        return verified;
    }
}
