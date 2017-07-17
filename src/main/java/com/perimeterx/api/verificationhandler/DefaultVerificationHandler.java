package com.perimeterx.api.verificationhandler;

import com.perimeterx.api.activities.ActivityHandler;
import com.perimeterx.api.blockhandler.BlockHandler;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.configuration.ModuleMode;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.exceptions.PXException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponseWrapper;

/**
 * Created by nitzangoldfeder on 28/05/2017.
 */
public class DefaultVerificationHandler implements VerificationHandler {

    private Logger logger = LoggerFactory.getLogger(DefaultVerificationHandler.class);

    private PXConfiguration pxConfiguration;
    private ActivityHandler activityHandler;
    private BlockHandler blockHandler;

    public DefaultVerificationHandler(PXConfiguration pxConfiguration, ActivityHandler activityHandler, BlockHandler blockHandler) {
        this.pxConfiguration = pxConfiguration;
        this.activityHandler = activityHandler;
        this.blockHandler = blockHandler;
    }

    @Override
    public boolean handleVerification(PXContext context, HttpServletResponseWrapper responseWrapper) throws PXException {
        boolean verified = shouldPassRequest(context);
        if (verified) {
            logger.info("Passing request {} {}", verified, this.pxConfiguration.getModuleMode());
            // Not blocking request and sending page_requested activity to px if configured as true
            if (this.pxConfiguration.shouldSendPageActivities()) {
                this.activityHandler.handlePageRequestedActivity(context);
            }
        } else {
            logger.info("Request invalid");
            this.activityHandler.handleBlockActivity(context);
            this.blockHandler.handleBlocking(context, this.pxConfiguration, responseWrapper);
        }
        return verified;
    }

    private boolean shouldPassRequest(PXContext context){
        int score = context.getScore();
        int blockingScore = this.pxConfiguration.getBlockingScore();
        // If should block this request we will apply our block handle and send the block activity to px
        boolean verified = score < blockingScore;
        logger.info("Request score: {}, Blocking score: {}", score, blockingScore);
        return verified || pxConfiguration.getModuleMode().equals(ModuleMode.MONITOR);
    }
}
