package com.perimeterx.models.httpmodels;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AdvancedBlockingResponse {

    @JsonProperty("appId")
    private String appId;

    @JsonProperty("jsClientSrc")
    private String jsClientSrc;

    @JsonProperty("firstPartyEnabled")
    private String firstPartyEnabled;

    @JsonProperty("vid")
    private String vid;

    @JsonProperty("uuid")
    private String uuid;

    @JsonProperty("hostUrl")
    private String hostUrl;

    @JsonProperty("blockScript")
    private String blockScript;

    @JsonProperty("altBlockScript")
    private String altBlockScript;

    public AdvancedBlockingResponse(String appId, String jsClientSrc, String firstPartyEnabled, String vid, String uuid, String hostUrl, String blockScript, String altBlockScript) {
        this.appId = appId;
        this.jsClientSrc = jsClientSrc;
        this.firstPartyEnabled = firstPartyEnabled;
        this.vid = vid;
        this.uuid = uuid;
        this.hostUrl = hostUrl;
        this.blockScript = blockScript;
        this.altBlockScript = altBlockScript;
    }
}
