package com.perimeterx.models.configuration;

import com.perimeterx.utils.Constants;
import org.apache.commons.lang3.Validate;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * PX configuration object
 * <p>
 * Created by shikloshi on 03/07/2016.
 */
public class PXConfiguration {

    private String appId;
    private String cookieKey;
    private String authToken;
    private boolean moduleEnabled;
    @Deprecated
    private boolean captchaEnabled;
    private boolean encryptionEnabled;
    private int blockingScore;
    private Set<String> sensitiveHeaders;
    private int maxBufferLen;
    private int apiTimeout;
    private int connectionTimeout;
    private boolean sendPageActivities;
    private boolean signedWithIP;
    private String serverURL;
    private String customLogo;
    private String cssRef;
    private String jsRef;
    private Set<String> sensitiveRoutes;
    private String checksum;
    private boolean remoteConfigurationEnabled;
    private ModuleMode moduleMode;

    private PXConfiguration(Builder builder) {
        appId = builder.appId;
        cookieKey = builder.cookieKey;
        authToken = builder.authToken;
        moduleEnabled = builder.moduleEnabled;
        encryptionEnabled = builder.encryptionEnabled;
        blockingScore = builder.blockingScore;
        sensitiveHeaders = builder.sensitiveHeaders;
        maxBufferLen = builder.maxBufferLen;
        apiTimeout = builder.apiTimeout;
        connectionTimeout = builder.connectionTimeout;
        sendPageActivities = builder.sendPageActivities;
        signedWithIP = builder.signedWithIP;
        serverURL = builder.serverURL;
        customLogo = builder.customLogo;
        cssRef = builder.cssRef;
        jsRef = builder.jsRef;
        sensitiveRoutes = builder.sensitiveRoutes;
        remoteConfigurationEnabled = builder.remoteConfigurationEnabled;
        moduleMode = builder.moduleMode;
    }

    public void updateConfigurationFromStub(PXConfigurationStub pxConfigurationStub){
        this.appId = pxConfigurationStub.getAppId();
        this.checksum = pxConfigurationStub.getChecksum();
        this.cookieKey = pxConfigurationStub.getCookieSecret();
        this.blockingScore = pxConfigurationStub.getBlockingScore();
        this.apiTimeout = pxConfigurationStub.getApiConnectTimeout();
        this.connectionTimeout = pxConfigurationStub.getApiConnectTimeout();
        this.sensitiveHeaders = pxConfigurationStub.getSensitiveHeaders();
        this.moduleEnabled = pxConfigurationStub.isModuleEnabled();
        this.moduleMode = pxConfigurationStub.getModuleMode();
    }

    public void disableModule(){
        this.moduleEnabled = false;
    }

    public String getAppId() {
        return appId;
    }

    public String getCookieKey() {
        return cookieKey;
    }

    public String getAuthToken() {
        return authToken;
    }

    public int getBlockingScore() {
        return blockingScore;
    }

    public boolean isModuleEnabled() {
        return moduleEnabled;
    }

    @Deprecated
    public boolean isCaptchaEnabled() {
        return captchaEnabled;
    }

    public boolean isEncryptionEnabled() {
        return encryptionEnabled;
    }

    public Set<String> getSensitiveHeaders() {
        return sensitiveHeaders;
    }

    public int getMaxBufferLen() {
        return maxBufferLen;
    }

    public int getApiTimeout() {
        return apiTimeout;
    }

    public int getConnectionTimeout() {
        return this.connectionTimeout;
    }

    public boolean shouldSendPageActivities() {
        return sendPageActivities;
    }

    public boolean wasSignedWithIP() {
        return signedWithIP;
    }

    public String getServerURL() {
        return serverURL;
    }

    public String getCssRef() {
        return cssRef;
    }

    public String getCustomLogo() {
        return customLogo;
    }

    public String getJsRef() {
        return jsRef;
    }

    public Set<String> getSensitiveRoutes() {
        return sensitiveRoutes;
    }

    public String getChecksum(){
        return this.checksum;
    }

    public boolean isRemoteConfigurationEnabled(){
        return this.remoteConfigurationEnabled;
    }

    public ModuleMode getModuleMode() {
        return moduleMode;
    }

    public static final class Builder {
        private String appId;
        private String cookieKey;
        private String authToken;
        private boolean moduleEnabled = true;
        @Deprecated
        private boolean captchaEnabled = true;
        private boolean encryptionEnabled = true;
        private int blockingScore = 70;
        private Set<String> sensitiveHeaders = new HashSet<>(Arrays.asList("cookie", "cookies"));
        private int maxBufferLen = 10;
        private int apiTimeout = 1000;
        private int connectionTimeout = 1000;
        private boolean sendPageActivities = true;
        private boolean signedWithIP = false;
        private String serverURL;
        private String customLogo;
        private String cssRef;
        private String jsRef;
        private Set<String> sensitiveRoutes = new HashSet<>();
        private boolean remoteConfigurationEnabled = false;
        private ModuleMode moduleMode = ModuleMode.BLOCKING;

        public Builder() {
        }

        public Builder appId(String val) {
            appId = val;
            if (serverURL == null) {
                serverURL = String.format(Constants.SERVER_URL, appId.toLowerCase());
            }
            return this;
        }

        public Builder cookieKey(String val) {
            cookieKey = val;
            return this;
        }

        public Builder authToken(String val) {
            authToken = val;
            return this;
        }

        public Builder moduleEnabled(boolean val) {
            moduleEnabled = val;
            return this;
        }

        @Deprecated
        public Builder captchaEnabled(boolean val) {
            captchaEnabled = val;
            return this;
        }

        public Builder encryptionEnabled(boolean val) {
            encryptionEnabled = val;
            return this;
        }

        public Builder blockingScore(int val) {
            blockingScore = val;
            return this;
        }

        public Builder addSensitiveHeader(String key) {
            this.sensitiveHeaders.add(key);
            return this;
        }

        public Builder sensitiveHeaders(Set<String> val) {
            sensitiveHeaders = val;
            return this;
        }

        public Builder maxBufferLen(int val) {
            maxBufferLen = val;
            return this;
        }

        public Builder apiTimeout(int val) {
            apiTimeout = val;
            return this;
        }

        public Builder sendPageActivites(boolean val) {
            sendPageActivities = val;
            return this;
        }

        public Builder signedWithIP(boolean val) {
            signedWithIP = val;
            return this;
        }

        public Builder baseURL(String val) {
            serverURL = val;
            return this;
        }

        public Builder customLogo(String val){
            customLogo = val;
            return this;
        }

        public Builder cssRef(String val){
            cssRef = val;
            return this;
        }

        public Builder jsRef(String val){
            jsRef = val;
            return this;
        }

        public Builder sensitiveRoutes(Set<String> val){
            sensitiveRoutes = val;
            return this;
        }

        public Builder remoteConfigurationEnabled(boolean val){
            remoteConfigurationEnabled = val;
            return this;
        }

        public Builder moduleMode(ModuleMode val){
            moduleMode = val;
            return this;
        }

        public Builder connectionTimeout(int val){
            connectionTimeout = val;
            return this;
        }

        public PXConfiguration build() {
            Validate.notEmpty(this.appId, "Application ID (appId) must be set");
            Validate.notEmpty(this.cookieKey, "Cookie Key (cookieKey) must be set");
            Validate.notEmpty(this.authToken, "Authentication Token (authToken) must be set");
            return new PXConfiguration(this);
        }
    }
}
