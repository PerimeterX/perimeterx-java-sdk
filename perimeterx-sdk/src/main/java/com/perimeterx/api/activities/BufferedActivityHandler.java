package com.perimeterx.api.activities;

import com.perimeterx.api.PerimeterX;
import com.perimeterx.http.PXClient;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.activities.*;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.exceptions.PXException;
import com.perimeterx.utils.Constants;
import com.perimeterx.utils.logger.IPXLogger;
import com.perimeterx.utils.logger.LogReason;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Buffered activities and sends them to PX servers when buffer is full
 * <p>
 * Created by nitzangoldfeder on 05/03/2017.
 */
public class BufferedActivityHandler implements ActivityHandler {
    private static final ExecutorService es = Executors.newFixedThreadPool(8);
    private static final IPXLogger logger = PerimeterX.globalLogger;

    private final int maxBufferLength;
    private volatile ConcurrentLinkedQueue<Activity> bufferedActivities = new ConcurrentLinkedQueue<>();
    private final AtomicInteger counter = new AtomicInteger(0);
    private PXConfiguration configuration;
    private PXClient client;
    private ReentrantLock lock = new ReentrantLock();

    public BufferedActivityHandler(PXClient client, PXConfiguration configuration) {
        this.configuration = configuration;
        this.client = client;
        maxBufferLength = configuration.getMaxBufferLen();
    }

    @Override
    public void handleBlockActivity(PXContext context) throws PXException {
        Activity activity = ActivityFactory.createActivity(Constants.ACTIVITY_BLOCKED, configuration.getAppId(), context);
        handleSendActivities(activity, context);
    }

    @Override
    public void handlePageRequestedActivity(PXContext context) throws PXException {
        Activity activity = ActivityFactory.createActivity(Constants.ACTIVITY_PAGE_REQUESTED, configuration.getAppId(), context);
        handleSendActivities(activity, context);
    }

    @Override
    public void handleEnforcerTelemetryActivity(PXConfiguration pxConfig, UpdateReason updateReason, PXContext context) {
        try {
            EnforcerTelemetryActivityDetails details = new EnforcerTelemetryActivityDetails(pxConfig, updateReason);
            EnforcerTelemetry enforcerTelemetry = new EnforcerTelemetry("enforcer_telemetry", pxConfig.getAppId(), details);
            this.client.sendEnforcerTelemetry(enforcerTelemetry, context);
        } catch (IOException e) {
            logger.debug("An error occurred while sending telemetry command");
        }
    }

    @Override
    public void handleAdditionalS2SActivity(PXContext context) throws PXException {
        final Activity activity = createAdditionalS2SActivity(context);
        handleSendActivities(activity, context);
    }

    public Activity createAdditionalS2SActivity(PXContext context) {
        final Activity activity = ActivityFactory.createActivity(Constants.ACTIVITY_ADDITIONAL_S2S, configuration.getAppId(), context);

        if (isRequireRawUsername(context)) {
            ((AdditionalS2SActivityDetails) activity.getDetails())
                    .setUsername(context.getLoginData().getLoginCredentials().getRawUsername());
        }
        return activity;
    }

    private boolean isRequireRawUsername(PXContext context) {
        final boolean loginRequestSentToOrigin = context.getLoginData().getLoginSuccessful() != null;

        return (!loginRequestSentToOrigin || context.getLoginData().getLoginSuccessful())
                && context.isBreachedAccount()
                && configuration.isAddRawUsernameOnAdditionalS2SActivity();
    }

    private void handleSendActivities(Activity activity, PXContext context) {
        bufferedActivities.add(activity);
        int count = counter.incrementAndGet();
        if (count > maxBufferLength) {
            handleOverflow(context);
        }
    }

    private void handleOverflow(PXContext context) {
        es.execute(() -> {
            if (lock.tryLock()) {
                try {
                    if (this.bufferedActivities.size() > this.maxBufferLength) {
                        ConcurrentLinkedQueue<Activity> activitiesToSend = flush();
                        sendAsync(activitiesToSend, context);
                    }
                } catch (Exception e) {
                    context.logger.error("failed to send async activities", e.getMessage());
                } finally {
                    lock.unlock();
                }
            } else {
                context.logger.debug("handleOverflow -Lock acquisition failed");
            }
        });
    }

    private void sendAsync(ConcurrentLinkedQueue<Activity> activitiesToSend, PXContext context) throws PXException {
        if (activitiesToSend == null) {
            return;
        }

        List<Activity> activitiesLocal = activitiesAsList(activitiesToSend);
        try {
            client.sendBatchActivities(activitiesLocal, context);
        } catch (Exception e) {
            throw new PXException(e);
        }

    }

    private List<Activity> activitiesAsList(ConcurrentLinkedQueue<Activity> activityQueue) {
        final int maxElements = maxBufferLength + 10;
        List<Activity> localActivityList = new ArrayList<>();
        for (int i = 0; i < maxElements && !activityQueue.isEmpty(); i++) {
            Activity activity = activityQueue.poll();
            localActivityList.add(activity);
        }
        return localActivityList;
    }

    private ConcurrentLinkedQueue<Activity> flush() {
        ConcurrentLinkedQueue<Activity> activitiesToSend = bufferedActivities;
        bufferedActivities = new ConcurrentLinkedQueue<>();
        counter.set(0);
        return activitiesToSend;
    }
}
