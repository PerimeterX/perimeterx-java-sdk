package com.perimeterx.models.configuration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

/**
 * Created by nitzangoldfeder on 19/06/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PXDynamicConfiguration {

    @JsonProperty("moduleEnabled")
    private boolean moduleEnabled;
    @JsonProperty("checksum")
    private String checksum;
    @JsonProperty("cookieKey")
    private String cookieSecret;
    @JsonProperty("appId")
    private String appId;
    @JsonProperty("blockingScore")
    private int blockingScore;
    @JsonProperty("moduleMode")
    private ModuleMode moduleMode;
    @JsonProperty("connectTimeout")
    private int connectTimeout;
    @JsonProperty("riskTimeout")
    private int riskRequestTimeout;
    @JsonProperty("sensitiveHeaders")
    private Set<String> sensitiveHeaders;
    @JsonProperty("ipHeaders")
    private Set<String> ipHeaders;

    public boolean isModuleEnabled() {
        return moduleEnabled;
    }

    public void setModuleEnabled(boolean moduleEnabled) {
        this.moduleEnabled = moduleEnabled;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public String getCookieSecret() {
        return cookieSecret;
    }

    public void setCookieSecret(String cookieSecert) {
        this.cookieSecret = cookieSecert;
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

    public ModuleMode getModuleMode() {
        return moduleMode;
    }

    public void setModuleMode(ModuleMode moduleMode) {
        this.moduleMode = moduleMode;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getRiskRequestTimeout() {
        return riskRequestTimeout;
    }

    public void setRiskRequestTimeout(int riskRequestTimeout) {
        this.riskRequestTimeout = riskRequestTimeout;
    }

    public Set<String> getSensitiveHeaders() {
        return this.sensitiveHeaders;
    }

    public void setSensitiveHeaders(Set<String> sensitiveHeaders) {
        this.sensitiveHeaders = sensitiveHeaders;
    }

    public Set<String> getIpHeaders() {
        return this.ipHeaders;
    }

    public void setIpHeaders(Set<String> ipHeaders) {
        this.ipHeaders = ipHeaders;
    }
}
