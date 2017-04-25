package com.perimeterx.api.activities;

import com.perimeterx.api.PXConfiguration;
import com.perimeterx.http.PXClient;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.activities.Activity;
import com.perimeterx.models.activities.ActivityFactory;
import com.perimeterx.models.exceptions.PXException;
import com.perimeterx.utils.Constants;

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
            this.client.sendActivity(activity);
        } catch (IOException e) {
            throw new PXException(e);
        }
    }

    @Override
    public void handlePageRequestedActivity(PXContext context) throws PXException {
        Activity activity = ActivityFactory.createActivity(Constants.ACTIVITY_PAGE_REQUESTED, configuration.getAppId(), context);
        try {
            this.client.sendActivity(activity);
        } catch (IOException e) {
            throw new PXException(e);
        }
    }
}