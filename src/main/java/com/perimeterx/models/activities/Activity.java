package com.perimeterx.models.activities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.perimeterx.models.PXContext;

import java.util.Map;

/**
 * Activity model
 * <p>
 * Created by shikloshi on 06/07/2016.
 */
public class Activity {

    private String type;
    private Map<String, String> headers;
    private long timestamp;
    @JsonProperty("socket_ip")
    private String socketIp;
    private String url;
    @JsonProperty("px_app_id")
    private String pxAppId;
    private String vid;
    private ActivityDetails details;

    public Activity(String activityType, String appId, PXContext context, ActivityDetails details) {
        this.type = activityType;
        this.headers = context.getHeaders();
        this.timestamp = System.currentTimeMillis();
        this.socketIp = context.getIp();
        this.pxAppId = appId;
        this.url = context.getFullUrl();
        this.vid = context.getVid();
        this.details = details;
    }

    public String getType() {
        return type;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getSocketIp() {
        return socketIp;
    }

    public String getUrl() {
        return url;
    }

    public String getPxAppId() {
        return pxAppId;
    }

    public String getVid() {
        return vid;
    }

    public ActivityDetails getDetails() {
        return details;
    }
}
