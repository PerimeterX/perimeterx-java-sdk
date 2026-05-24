package com.perimeterx.models.activities;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by nitzangoldfeder on 29/10/2017.
 */
public class EnforcerTelemetry {
    private String type;
    private long timestamp;
    @JsonProperty("px_app_id")
    private String pxAppId;
    private ActivityDetails details;

    public EnforcerTelemetry(String activityType, String appId, ActivityDetails details) {
        this.type = activityType;
        this.timestamp = System.currentTimeMillis();
        this.pxAppId = appId;
        this.details = details;
    }

    public String getType() {
        return type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getPxAppId() {
        return pxAppId;
    }

    public ActivityDetails getDetails() {
        return details;
    }
}

