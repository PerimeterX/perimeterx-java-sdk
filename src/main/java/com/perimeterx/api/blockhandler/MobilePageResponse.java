package com.perimeterx.api.blockhandler;

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

    public MobilePageResponse(String action, String uuid, String appId, String page) {
        this.action = action;
        this.uuid = uuid;
        this.appId = appId;
        this.page = page;
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
}
