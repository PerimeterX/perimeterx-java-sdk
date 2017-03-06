package com.perimeterx.api.activities;

import com.perimeterx.api.PXConfiguration;
import com.perimeterx.http.PXClient;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.activities.Activity;
import com.perimeterx.models.activities.ActivityFactory;
import com.perimeterx.models.exceptions.PXException;
import com.perimeterx.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nitzangoldfeder on 05/03/2017.
 */
public class BufferedActivityHandler implements ActivityHandler {

    private int maxBufferLength;
    private List<Activity> bufferedActivities;
    private PXConfiguration configuration;
    private PXClient client;

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

    public void handleSendActivities(Activity activity) throws PXException {
        add(activity);
        if (isFull()) {
            flush();
            clear();
        }
    }

    public void add(Activity activity) {
        bufferedActivities.add(activity);
    }

    public int size() {
        return bufferedActivities.size();
    }

    public boolean isFull() {
        return size() >= this.maxBufferLength;
    }

    /**
     * Will trigger the client to send a batch of activities to PX Servers
     * Most likely to call clear after to remove sent activities
     *
     * @throws PXException
     */
    public void flush() throws PXException {
        try {
            client.sendBatchActivities(bufferedActivities);
        } catch (Exception e) {
            throw new PXException(e);
        }
    }

    public void clear() {
        bufferedActivities.clear();
    }
}
