package com.perimeterx.api.activities;

import com.perimeterx.http.PXClient;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.activities.*;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.exceptions.PXException;
import com.perimeterx.utils.Constants;
import com.perimeterx.utils.logger.LogReason;

import java.io.IOException;

/**
 * Simple activity send per server request
 * <p>
 * Created by shikloshi on 05/07/2016.
 */
public class DefaultActivityHandler implements ActivityHandler {

    private PXConfiguration configuration;
    private PXClient client;

    public DefaultActivityHandler(PXClient client, PXConfiguration configuration) {
        this.configuration = configuration;
        this.client = client;
    }

    @Override
    public void handleBlockActivity(PXContext context) throws PXException {
        Activity activity = ActivityFactory.createActivity(Constants.ACTIVITY_BLOCKED, configuration.getAppId(), context);
        try {
            this.client.sendActivity(activity, context);
        } catch (IOException e) {
            throw new PXException(LogReason.ERROR_HANDLE_BLOCK_ACTIVITY + ". Reason: " + e.getMessage(), e);
        }
    }

    @Override
    public void handlePageRequestedActivity(PXContext context) throws PXException {
        Activity activity = ActivityFactory.createActivity(Constants.ACTIVITY_PAGE_REQUESTED, configuration.getAppId(), context);
        try {
            this.client.sendActivity(activity, context);
        } catch (IOException e) {
            throw new PXException(LogReason.ERROR_HANDLE_PAGE_REQUESTED + ". Reason: " + e.getMessage(), e);
        }
    }

    @Override
    public void handleEnforcerTelemetryActivity(PXConfiguration pxConfiguration, UpdateReason updateReason, PXContext context) throws PXException {
        try {
            EnforcerTelemetryActivityDetails details = new EnforcerTelemetryActivityDetails(pxConfiguration, updateReason);
            EnforcerTelemetry enforcerTelemetry = new EnforcerTelemetry("enforcer_telemetry", pxConfiguration.getAppId(), details);
            this.client.sendEnforcerTelemetry(enforcerTelemetry, context);
        } catch (Exception e) {
            context.logger.debug("An error occurred while sending telemetry command");
            throw new PXException(LogReason.ERROR_TELEMETRY_EXCEPTION + ". Reason: " + e.getMessage(), e);
        }
    }

    //TODO - Should be populated when Adding unit tests
    @Override
    public void handleAdditionalS2SActivity(PXContext context) throws PXException {

    }
}