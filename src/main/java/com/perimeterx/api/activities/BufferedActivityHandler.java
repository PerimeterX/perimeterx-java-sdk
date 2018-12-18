package com.perimeterx.api.activities;

import com.perimeterx.http.PXClient;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.activities.*;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.exceptions.PXException;
import com.perimeterx.utils.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Buffered activities and sends them to PX servers when buffer is full
 * <p>
 * Created by nitzangoldfeder on 05/03/2017.
 */
public class BufferedActivityHandler implements ActivityHandler {

    private int maxBufferLength;
    private List<Activity> bufferedActivities;
    private PXConfiguration configuration;
    private PXClient client;
    private Object lock = new Object();

    public BufferedActivityHandler(PXClient client, PXConfiguration configuration) {
        this.configuration = configuration;
        this.client = client;
        this.maxBufferLength = configuration.getMaxBufferLen();
        this.bufferedActivities = new ArrayList<>();
    }

    @Override
    public void handleBlockActivity(PXContext context) throws PXException {
        Activity activity = ActivityFactory.createActivity(Constants.ACTIVITY_BLOCKED, configuration.getAppId(), context);
        handleSendActivities(activity);
    }

    @Override
    public void handlePageRequestedActivity(PXContext context) throws PXException {
        Activity activity = ActivityFactory.createActivity(Constants.ACTIVITY_PAGE_REQUESTED, configuration.getAppId(), context);
        handleSendActivities(activity);
    }

    @Override
    public void handleEnforcerTelemetryActivity(PXConfiguration pxConfig, UpdateReason updateReason) throws PXException {
        try {
            EnforcerTelemetryActivityDetails details = new EnforcerTelemetryActivityDetails(pxConfig, updateReason);
            EnforcerTelemetry enforcerTelemetry = new EnforcerTelemetry("enforcer_telemetry", pxConfig.getAppId(), details);
            this.client.sendEnforcerTelemetry(enforcerTelemetry);
        } catch (IOException e) {
            throw new PXException(e);
        }
    }

    private void handleSendActivities(Activity activity) throws PXException {
        bufferedActivities.add(activity);
        if (bufferedActivities.size() >= maxBufferLength) {
            flush();
            bufferedActivities.clear();
        }
    }

    /**
     * Will trigger the client to send a batch of activities to PX Servers
     * Most likely to call clear after to remove sent activities
     *
     * @throws PXException - when transport layer fails to report activities
     */
    private void flush() throws PXException {
        synchronized (lock) {
            try {
                client.sendBatchActivities(bufferedActivities);
            } catch (Exception e) {
                throw new PXException(e);
            }
        }
    }
}
