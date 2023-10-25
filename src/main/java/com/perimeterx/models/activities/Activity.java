package com.perimeterx.models.activities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.perimeterx.models.PXContext;
import com.perimeterx.utils.Constants;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Activity model
 * <p>
 * Created by shikloshi on 06/07/2016.
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Activity {

    private String type;

    @JsonProperty("headers")
    private List<ActivityHeader> headers;

    private long timestamp;
    @JsonProperty("socket_ip")
    private String socketIp;
    private String url;
    @JsonProperty("px_app_id")
    private String pxAppId;
    private String vid;
    private ActivityDetails details;
    private String pxhd;

    public Activity(String activityType, String appId, PXContext context, ActivityDetails details) {
        this.type = activityType;
        this.headers = !activityType.equals(Constants.ACTIVITY_ADDITIONAL_S2S) ? getActivityHeaders(context.getHeaders(), context.getSensitiveHeaders()) : null;
        this.timestamp = System.currentTimeMillis();
        this.socketIp = context.getIp();
        this.pxAppId = appId;
        this.url = context.getFullUrl();
        this.vid = context.getVid();
        this.details = details;

        if ((activityType.equals(Constants.ACTIVITY_PAGE_REQUESTED) || activityType.equals(Constants.ACTIVITY_BLOCKED)) && context.getPxhd() != null) {
            this.pxhd = context.getPxhd();
        }
    }

    private List<ActivityHeader> getActivityHeaders(Map<String, String> headers, Set<String> sensitiveHeaders) {
        if (headers == null || headers.isEmpty()) {
            return null;
        }
        return headers.entrySet().stream()
                .filter(entry -> !sensitiveHeaders.contains(entry.getKey()))
                .map(entry -> new ActivityHeader(entry.getKey(), entry.getValue())).collect(Collectors.toList());
    }
}
