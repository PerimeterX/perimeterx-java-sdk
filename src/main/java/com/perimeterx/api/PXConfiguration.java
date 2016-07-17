package com.perimeterx.api;

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

    private final static String sdkName = "JAVA SDK v1.0";

    private String appId;
    private String cookieKey;
    private String authToken;
    private boolean moduleEnabled;
    private boolean captchaEnabled;
    private boolean encryptionEnabled;
    private int blockingScore;
    private Set<String> sensitiveHeaders;
    private int maxBufferLen;
    private int apiTimeout;
    private boolean debugMode;
    private boolean sendPageActivities;

    private PXConfiguration(Builder builder) {
        appId = builder.appId;
        cookieKey = builder.cookieKey;
        authToken = builder.authToken;
        moduleEnabled = builder.moduleEnabled;
        captchaEnabled = builder.captchaEnabled;
        encryptionEnabled = builder.encryptionEnabled;
        blockingScore = builder.blockingScore;
        sensitiveHeaders = builder.sensitiveHeaders;
        maxBufferLen = builder.maxBufferLen;
        apiTimeout = builder.apiTimeout;
        debugMode = builder.debugMode;
        sendPageActivities = builder.sendPageActivities;
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

    public boolean isSendPageActivities() {
        return sendPageActivities;
    }

    public String getSdkName() {
        return sdkName;
    }

    public static final class Builder {
        private String appId;
        private String cookieKey;
        private String authToken;
        private boolean moduleEnabled = true;
        private boolean captchaEnabled = true;
        private boolean encryptionEnabled = true;
        private int blockingScore = 70;
        private Set<String> sensitiveHeaders = new HashSet<>(Arrays.asList("cookie", "cookies"));
        private int maxBufferLen = 1;
        private int apiTimeout = 1000;
        private boolean debugMode = false;
        private boolean sendPageActivities = false;

        public Builder() {
        }

        public Builder appId(String val) {
            appId = val;
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

        public Builder debugeMode(boolean val) {
            debugMode = val;
            return this;
        }

        public Builder sendPageActivites(boolean val) {
            sendPageActivities = val;
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
