package com.perimeterx.models.httpmodels;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MobilePageResponse {

    @JsonProperty("action")
    private String action;

    @JsonProperty("uuid")
    private String uuid;

    @JsonProperty("appId")
    private String appId;

    @JsonProperty("page")
    private String page;

    @JsonProperty("collectorUrl")
    private String collectorUrl;

    public MobilePageResponse(String action, String uuid, String appId, String page, String collectorUrl) {
        this.action = action;
        this.uuid = uuid;
        this.appId = appId;
        this.page = page;
        this.collectorUrl = collectorUrl;
    }

    public String getAction() {
        return action;
    }

    public String getUuid() {
        return uuid;
    }

    public String getAppId() {
        return appId;
    }

    public String getPage() {
        return page;
    }

    public String getCollectorUrl() {
        return collectorUrl;
    }
}