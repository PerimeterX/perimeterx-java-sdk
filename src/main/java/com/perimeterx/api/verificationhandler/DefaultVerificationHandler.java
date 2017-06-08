package com.perimeterx.api.verificationhandler;

import com.perimeterx.api.PXConfiguration;
import com.perimeterx.api.PerimeterX;
import com.perimeterx.api.activities.ActivityHandler;
import com.perimeterx.api.blockhandler.BlockHandler;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.exceptions.PXException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponseWrapper;

/**
 * Created by nitzangoldfeder on 28/05/2017.
 */
public class DefaultVerificationHandler implements VerificationHandler {

    private Logger logger = LoggerFactory.getLogger(PerimeterX.class);

    private  PXConfiguration configuration;
    private ActivityHandler activityHandler;
    private BlockHandler blockHandler;

    public DefaultVerificationHandler(PXConfiguration pxConfiguration, ActivityHandler activityHandler, BlockHandler blockHandler){
        this.configuration = pxConfiguration;
        this.activityHandler = activityHandler;
        this.blockHandler = blockHandler;
    }

    @Override
    public boolean handleVerification(PXContext context, HttpServletResponseWrapper responseWrapper) throws PXException {
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
            this.activityHandler.handleBlockActivity(context);
            this.blockHandler.handleBlocking(context, this.configuration, responseWrapper);
        }
        return verified;
    }
}
