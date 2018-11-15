package com.perimeterx.models.activities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.perimeterx.models.PXContext;
import com.perimeterx.utils.Constants;
import lombok.Getter;

import java.util.Map;

/**
 * Activity model
 * <p>
 * Created by shikloshi on 06/07/2016.
 */
@Getter
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
    private String pxhd;
    @JsonProperty("block_action")
    private String blockAction;

    public Activity(String activityType, String appId, PXContext context, ActivityDetails details) {
        this.type = activityType;
        this.headers = context.getHeaders();
        this.timestamp = System.currentTimeMillis();
        this.socketIp = context.getIp();
        this.pxAppId = appId;
        this.url = context.getFullUrl();
        this.vid = context.getVid();
        this.details = details;
        if (activityType.equals(Constants.ACTIVITY_PAGE_REQUESTED) && context.getPxhd() != null) {
            this.pxhd = context.getPxhd();
        }
        if (activityType.equals(Constants.ACTIVITY_BLOCKED) && context.getBlockAction() != null) {
            this.blockAction = context.getBlockAction().getCode();
        }
    }

}
