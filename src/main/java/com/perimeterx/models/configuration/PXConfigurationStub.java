package com.perimeterx.models.configuration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by nitzangoldfeder on 19/06/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PXConfigurationStub {
    @JsonProperty("checksum")
    private String checksum;
    @JsonProperty("cookieKey")
    private String cookieSecert;
    @JsonProperty("appId")
    private String appId;
    @JsonProperty("blockingScore")
    private int blockingScore;
    @JsonProperty("debugMode")
    private boolean debugMode;
    @JsonProperty("scoreHeader")
    private String scoreHeaderName;
    @JsonProperty("moduleMode")
    private String moduleMode;
    @JsonProperty("connectTimeout")
    private int apiConnectTimeout;
    @JsonProperty("riskTimeout")
    private int s2sTimeout;
    @JsonProperty("blockPageTemplate")
    private String templateBlockPage;
    @JsonProperty("captchaPageTemplate")
    private String templateCaptchaPage;
    @JsonProperty("monitoringMode")
    private String monitoringMode;

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public String getCookieSecert() {
        return cookieSecert;
    }

    public void setCookieSecert(String cookieSecert) {
        this.cookieSecert = cookieSecert;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public int getBlockingScore() {
        return blockingScore;
    }

    public void setBlockingScore(int blockingScore) {
        this.blockingScore = blockingScore;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    public String getScoreHeaderName() {
        return scoreHeaderName;
    }

    public void setScoreHeaderName(String scoreHeaderName) {
        this.scoreHeaderName = scoreHeaderName;
    }

    public String getModuleMode() {
        return moduleMode;
    }

    public void setModuleMode(String moduleMode) {
        this.moduleMode = moduleMode;
    }

    public int getApiConnectTimeout() {
        return apiConnectTimeout;
    }

    public void setApiConnectTimeout(int apiConnectTimeout) {
        this.apiConnectTimeout = apiConnectTimeout;
    }

    public int getS2sTimeout() {
        return s2sTimeout;
    }

    public void setS2sTimeout(int s2sTimeout) {
        this.s2sTimeout = s2sTimeout;
    }

    public String getTemplateBlockPage() {
        return templateBlockPage;
    }

    public void setTemplateBlockPage(String templateBlockPage) {
        this.templateBlockPage = templateBlockPage;
    }

    public String getTemplateCaptchaPage() {
        return templateCaptchaPage;
    }

    public void setTemplateCaptchaPage(String templateCaptchaPage) {
        this.templateCaptchaPage = templateCaptchaPage;
    }

    public String getMonitoringMode(){
        return this.monitoringMode;
    }

    public void setMonitoringMode(String monitoringMode){
        this.monitoringMode = monitoringMode;
    }
}
