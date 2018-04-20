package com.perimeterx.models.configuration;

import com.perimeterx.api.blockhandler.BlockHandler;
import com.perimeterx.api.blockhandler.DefaultBlockHandler;
import com.perimeterx.api.providers.CustomParametersProvider;
import com.perimeterx.api.providers.DefaultCustomParametersProvider;
import com.perimeterx.models.risk.CustomParameters;
import com.perimeterx.utils.Constants;
import com.perimeterx.utils.PXLogger;
import java.util.*;

/**
 * PX configuration object
 * <p>
 * Created by shikloshi on 03/07/2016.
 */
public class PXConfiguration {
    private static final PXLogger logger = PXLogger.getLogger(PXConfiguration.class);

    private String appId;
    private String cookieKey;
    private String authToken;
    private boolean moduleEnabled;
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
    private Set<String> ipHeaders;
    private String checksum;
    private boolean remoteConfigurationEnabled;
    private ModuleMode moduleMode;
    private int remoteConfigurationInterval;
    private int remoteConfigurationDelay;
    private int maxConnections;
    private int maxConnectionsPerRoute;
    private String remoteConfigurationUrl;
    private CaptchaProvider captchaProvider;
    private CustomParametersProvider customParametersProvider;
    private BlockHandler blockHandler;

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
        remoteConfigurationInterval = builder.remoteConfigurationInterval;
        remoteConfigurationDelay = builder.remoteConfigurationDelay;
        maxConnections = builder.maxConnections;
        maxConnectionsPerRoute = builder.maxConnectionsPerRoute;
        remoteConfigurationUrl = builder.remoteConfigurationUrl;
        captchaProvider = builder.captchaProvider;
        ipHeaders = builder.ipHeaders;
        customParametersProvider = builder.customParametersProvider;
        blockHandler = builder.blockHandler;
    }

    private PXConfiguration(String appId, String cookieKey, String authToken, boolean moduleEnabled, boolean encryptionEnabled,
                            int blockingScore, Set<String> sensitiveHeaders, int maxBufferLen, int apiTimeout, int connectionTimeout,
                            boolean sendPageActivities, boolean signedWithIP, String serverURL, String customLogo, String cssRef,
                            String jsRef, Set<String> sensitiveRoutes, Set<String> ipHeaders, String checksum, boolean remoteConfigurationEnabled,
                            ModuleMode moduleMode, int remoteConfigurationInterval, int remoteConfigurationDelay, int maxConnections, int maxConnectionsPerRoute,
                            String remoteConfigurationUrl, CaptchaProvider captchaProvider, CustomParametersProvider customParametersProvider,
                            BlockHandler blockHandler) {
        this.appId = appId;
        this.cookieKey = cookieKey;
        this.authToken = authToken;
        this.moduleEnabled = moduleEnabled;
        this.encryptionEnabled = encryptionEnabled;
        this.blockingScore = blockingScore;
        this.sensitiveHeaders = sensitiveHeaders;
        this.maxBufferLen = maxBufferLen;
        this.apiTimeout = apiTimeout;
        this.connectionTimeout = connectionTimeout;
        this.sendPageActivities = sendPageActivities;
        this.signedWithIP = signedWithIP;
        this.serverURL = serverURL;
        this.customLogo = customLogo;
        this.cssRef = cssRef;
        this.jsRef = jsRef;
        this.sensitiveRoutes = sensitiveRoutes;
        this.ipHeaders = ipHeaders;
        this.checksum = checksum;
        this.remoteConfigurationEnabled = remoteConfigurationEnabled;
        this.moduleMode = moduleMode;
        this.remoteConfigurationInterval = remoteConfigurationInterval;
        this.remoteConfigurationDelay = remoteConfigurationDelay;
        this.maxConnections = maxConnections;
        this.maxConnectionsPerRoute = maxConnectionsPerRoute;
        this.remoteConfigurationUrl = remoteConfigurationUrl;
        this.captchaProvider = captchaProvider;
        this.customParametersProvider = customParametersProvider;
        this.blockHandler = blockHandler;
    }

    /*
    * @return Configuration Object clone without cookieKey and authToken
    * */
    public PXConfiguration getTelemetryConfig() {
        return new PXConfiguration(appId, null, null, moduleEnabled, encryptionEnabled, blockingScore, sensitiveHeaders, maxBufferLen, apiTimeout,
                connectionTimeout, sendPageActivities, signedWithIP, serverURL, customLogo, cssRef, jsRef, sensitiveRoutes, ipHeaders, checksum, remoteConfigurationEnabled,
                moduleMode, remoteConfigurationInterval, remoteConfigurationDelay, maxConnections, maxConnectionsPerRoute, remoteConfigurationUrl, captchaProvider,
                customParametersProvider, blockHandler);
    }

    public String getRemoteConfigurationUrl(){
        return this.remoteConfigurationUrl;
    }

    public void disableModule() {
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

    public String getChecksum() {
        return this.checksum;
    }

    public boolean isRemoteConfigurationEnabled() {
        return this.remoteConfigurationEnabled;
    }

    public ModuleMode getModuleMode() {
        return moduleMode;
    }

    public int getRemoteConfigurationInterval(){
        return this.remoteConfigurationInterval;
    }

    public int getRemoteConfigurationDelay(){
        return this.remoteConfigurationDelay;
    }

    public int getMaxConnections(){
        return this.maxConnections;
    }

    public int getMaxConnectionsPerRoute(){
        return this.maxConnectionsPerRoute;
    }

    public CaptchaProvider getCaptchaProvider() {
        return captchaProvider;
    }

    public Set<String> getIpHeaders() {
        return ipHeaders;
    }

    public CustomParametersProvider getCustomParametersProvider() {
        return customParametersProvider;
    }

    public BlockHandler getBlockHandler() {
        return blockHandler;
    }

    public void update(PXDynamicConfiguration pxDynamicConfiguration) {
        logger.debug("Updating PXConfiguration file");
        this.appId = pxDynamicConfiguration.getAppId();
        this.checksum = pxDynamicConfiguration.getChecksum();
        this.cookieKey = pxDynamicConfiguration.getCookieSecret();
        this.blockingScore = pxDynamicConfiguration.getBlockingScore();
        this.apiTimeout = pxDynamicConfiguration.getApiConnectTimeout();
        this.connectionTimeout = pxDynamicConfiguration.getApiConnectTimeout();
        this.sensitiveHeaders = pxDynamicConfiguration.getSensitiveHeaders();
        this.moduleEnabled = pxDynamicConfiguration.isModuleEnabled();
        this.moduleMode = pxDynamicConfiguration.getModuleMode();
        this.ipHeaders = pxDynamicConfiguration.getIpHeaders();
    }

    public static final class Builder {
        private String appId;
        private String cookieKey;
        private String authToken;
        private boolean moduleEnabled = true;
        private boolean encryptionEnabled = true;
        private int blockingScore = 100;
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
        private ModuleMode moduleMode = ModuleMode.MONITOR;
        private int remoteConfigurationInterval = 1000 * 5;
        private int remoteConfigurationDelay = 0;
        private int maxConnectionsPerRoute = 20;
        private int maxConnections = 200;
        private String remoteConfigurationUrl = Constants.REMOTE_CONFIGURATION_SERVER_URL;
        private CaptchaProvider captchaProvider = CaptchaProvider.RECAPTCHA;
        private Set<String> ipHeaders = new HashSet<>();
        private CustomParametersProvider customParametersProvider = new DefaultCustomParametersProvider();
        private BlockHandler blockHandler = new DefaultBlockHandler();

        public Builder() {
        }

        public Builder remoteConfigurationUrl(String val) {
            remoteConfigurationUrl = val;
            return this;
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

        public Builder customLogo(String val) {
            customLogo = val;
            return this;
        }

        public Builder cssRef(String val) {
            cssRef = val;
            return this;
        }

        public Builder jsRef(String val) {
            jsRef = val;
            return this;
        }

        public Builder sensitiveRoutes(Set<String> val) {
            sensitiveRoutes = val;
            return this;
        }

        public Builder remoteConfigurationEnabled(boolean val) {
            remoteConfigurationEnabled = val;
            return this;
        }

        public Builder moduleMode(ModuleMode val) {
            moduleMode = val;
            return this;
        }

        public Builder connectionTimeout(int val) {
            connectionTimeout = val;
            return this;
        }

        public Builder remoteConfigurationInterval(int val) {
            remoteConfigurationInterval = val;
            return this;
        }

        public Builder remoteConfigurationDelay(int val) {
            remoteConfigurationDelay = val;
            return this;
        }

        public Builder maxConnection(int val){
            maxConnections = val;
            return this;
        }

        public Builder maxConnectionsPerRoute(int val){
            maxConnectionsPerRoute = val;
            return this;
        }

        public Builder captchaProvider(CaptchaProvider captchaProvider) {
            this.captchaProvider = captchaProvider;
            return this;
        }

        public Builder ipHeaders(Set<String> ipHeaders) {
            this.ipHeaders = ipHeaders;
            return this;
        }

        public Builder customParametersProvider(CustomParametersProvider customParametersProvider) {
            this.customParametersProvider = customParametersProvider;
            return this;
        }

        public Builder blockHandler(BlockHandler blockHandler) {
            this.blockHandler = blockHandler;
            return this;
        }

        public PXConfiguration build() {
            if (!this.remoteConfigurationEnabled) {
                notEmpty(this.appId, "appId");
                notEmpty(this.cookieKey, "cookieKey");
            }
            notEmpty(this.authToken, "authToken");
            return new PXConfiguration(this);
        }

        private void notEmpty(String configValue, String configName) {
            if (configValue == null || configValue.isEmpty()) {
                logger.error(PXLogger.LogReason.ERROR_CONFIGURATION_MISSING_MANDATORY_CONFIGURATION, configName);
                throw new IllegalArgumentException(String.format("missing mandatory configuration. %s", configName));
            }
        }
    }
}