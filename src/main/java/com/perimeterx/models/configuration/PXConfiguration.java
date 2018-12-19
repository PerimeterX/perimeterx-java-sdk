package com.perimeterx.models.configuration;

import com.perimeterx.api.blockhandler.BlockHandler;
import com.perimeterx.api.blockhandler.DefaultBlockHandler;
import com.perimeterx.api.providers.CustomParametersProvider;
import com.perimeterx.api.providers.DefaultCustomParametersProvider;
import com.perimeterx.utils.Constants;
import com.perimeterx.utils.PXLogger;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
    private int riskRequestTimeout;
    private int offlineRequestTimeout;
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
    private CustomParametersProvider customParametersProvider;
    private BlockHandler blockHandler;
    private String collectorUrl;
    private String clientHost;
    private boolean firstPartyEnabled;
    private boolean xhrFirstPartyEnabled;
    private boolean useProxy;
    private String proxyHost;
    private int proxyPort;
    private boolean testingMode;
    private int validateRequestQueueInterval;

    private PXConfiguration(Builder builder) {
        appId = builder.appId;
        cookieKey = builder.cookieKey;
        authToken = builder.authToken;
        moduleEnabled = builder.moduleEnabled;
        encryptionEnabled = builder.encryptionEnabled;
        blockingScore = builder.blockingScore;
        sensitiveHeaders = builder.sensitiveHeaders;
        maxBufferLen = builder.maxBufferLen;
        riskRequestTimeout = builder.riskRequestTimeout;
        offlineRequestTimeout = builder.offlineRequestTimeout;
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
        ipHeaders = builder.ipHeaders;
        customParametersProvider = builder.customParametersProvider;
        blockHandler = builder.blockHandler;
        collectorUrl = builder.collectorUrl;
        firstPartyEnabled = builder.firstPartyEnabled;
        xhrFirstPartyEnabled = builder.xhrFirstPartyEnabled;
        clientHost = builder.clientHost;
        useProxy = builder.useProxy;
        proxyHost = builder.proxyHost;
        proxyPort = builder.proxyPort;
        testingMode = builder.testingMode;
        validateRequestQueueInterval = builder.validateRequestQueueInterval;
    }

    private PXConfiguration(String appId, String cookieKey, String authToken, boolean moduleEnabled, boolean encryptionEnabled,
                            int blockingScore, Set<String> sensitiveHeaders, int maxBufferLen, int riskRequestTimeout, int offlineRequestTimeout,
                            boolean sendPageActivities, boolean signedWithIP, String serverURL, String customLogo, String cssRef,
                            String jsRef, Set<String> sensitiveRoutes, Set<String> ipHeaders, String checksum, boolean remoteConfigurationEnabled,
                            ModuleMode moduleMode, int remoteConfigurationInterval, int remoteConfigurationDelay, int maxConnections, int maxConnectionsPerRoute,
                            String remoteConfigurationUrl, CustomParametersProvider customParametersProvider,
                            BlockHandler blockHandler, String collectorUrl, boolean firstPartyEnabled, boolean xhrFirstPartyEnabled,
                            String clientHost, boolean useProxy, String proxyHost, int proxyPort) {
        this.appId = appId;
        this.cookieKey = cookieKey;
        this.authToken = authToken;
        this.moduleEnabled = moduleEnabled;
        this.encryptionEnabled = encryptionEnabled;
        this.blockingScore = blockingScore;
        this.sensitiveHeaders = sensitiveHeaders;
        this.maxBufferLen = maxBufferLen;
        this.riskRequestTimeout = riskRequestTimeout;
        this.offlineRequestTimeout = offlineRequestTimeout;
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
        this.customParametersProvider = customParametersProvider;
        this.blockHandler = blockHandler;
        this.collectorUrl = collectorUrl;
        this.firstPartyEnabled = firstPartyEnabled;
        this.xhrFirstPartyEnabled = xhrFirstPartyEnabled;
        this.clientHost = clientHost;
        this.useProxy = useProxy;
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
    }

    /**
     * @return Configuration Object clone without cookieKey and authToken
     **/
    public PXConfiguration getTelemetryConfig() {
        return new PXConfiguration(appId, null, null, moduleEnabled, encryptionEnabled, blockingScore, sensitiveHeaders, maxBufferLen, riskRequestTimeout,
                offlineRequestTimeout, sendPageActivities, signedWithIP, serverURL, customLogo, cssRef, jsRef, sensitiveRoutes, ipHeaders, checksum, remoteConfigurationEnabled,
                moduleMode, remoteConfigurationInterval, remoteConfigurationDelay, maxConnections, maxConnectionsPerRoute, remoteConfigurationUrl,
                customParametersProvider, blockHandler, collectorUrl, firstPartyEnabled, xhrFirstPartyEnabled, clientHost, useProxy, proxyHost, proxyPort);
    }

    public String getRemoteConfigurationUrl() {
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

    public int getRiskRequestTimeout() {
        return riskRequestTimeout;
    }

    public int getOfflineRequestTimeout() {
        return this.offlineRequestTimeout;
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

    public int getRemoteConfigurationInterval() {
        return this.remoteConfigurationInterval;
    }

    public int getRemoteConfigurationDelay() {
        return this.remoteConfigurationDelay;
    }

    public int getMaxConnections() {
        return this.maxConnections;
    }

    public int getMaxConnectionsPerRoute() {
        return this.maxConnectionsPerRoute;
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

    public String getCollectorUrl() {
        return collectorUrl;
    }

    public boolean isFirstPartyEnabled() {
        return firstPartyEnabled;
    }

    public boolean isXhrFirstPartyEnabled() {
        return xhrFirstPartyEnabled;
    }

    public String getClientHost() {
        return clientHost;
    }

    public boolean shouldUseProxy() {
        return useProxy;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public boolean isTestingMode() {
        return testingMode;
    }

    public int getValidateRequestQueueInterval() {
        return validateRequestQueueInterval;
    }

    public void update(PXDynamicConfiguration pxDynamicConfiguration) {
        logger.debug("Updating PXConfiguration file");
        this.appId = pxDynamicConfiguration.getAppId();
        this.checksum = pxDynamicConfiguration.getChecksum();
        this.cookieKey = pxDynamicConfiguration.getCookieSecret();
        this.blockingScore = pxDynamicConfiguration.getBlockingScore();
        this.riskRequestTimeout = pxDynamicConfiguration.getRiskRequestTimeout();
        this.sensitiveHeaders = pxDynamicConfiguration.getSensitiveHeaders();
        this.moduleEnabled = pxDynamicConfiguration.isModuleEnabled();
        this.moduleMode = pxDynamicConfiguration.getModuleMode();
        this.ipHeaders = pxDynamicConfiguration.getIpHeaders();
    }

    public static final class Builder {
        private boolean testingMode;
        private String appId;
        private String cookieKey;
        private String authToken;
        private boolean moduleEnabled = true;
        private boolean encryptionEnabled = true;
        private int blockingScore = 100;
        private Set<String> sensitiveHeaders = new HashSet<>(Arrays.asList("cookieOrig", "cookies"));
        private int maxBufferLen = 50;
        private int riskRequestTimeout = 1000;
        private int offlineRequestTimeout = 5000;
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
        private int maxConnectionsPerRoute = 50;
        private int maxConnections = 200;
        private String remoteConfigurationUrl = Constants.REMOTE_CONFIGURATION_SERVER_URL;
        private Set<String> ipHeaders = new HashSet<>();
        private CustomParametersProvider customParametersProvider = new DefaultCustomParametersProvider();
        private BlockHandler blockHandler = new DefaultBlockHandler();
        private String collectorUrl;
        private boolean xhrFirstPartyEnabled = true;
        private boolean firstPartyEnabled = true;
        private String clientHost = Constants.CLIENT_HOST;
        private boolean useProxy;
        private String proxyHost;
        private int proxyPort;
        private int validateRequestQueueInterval = 5 * 1000;

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

            if (collectorUrl == null) {
                collectorUrl = String.format(Constants.COLLECTOR_URL, appId.toLowerCase());
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

        public Builder useProxy(boolean val) {
            useProxy = val;
            return this;
        }

        public Builder proxyHost(String val) {
            proxyHost = val;
            return this;
        }

        public Builder proxyPort(int val) {
            proxyPort = val;
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

        public Builder riskRequestTimeout(int val) {
            riskRequestTimeout = val;
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

        public Builder offlineRequestTimeout(int val) {
            offlineRequestTimeout = val;
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

        public Builder maxConnection(int val) {
            maxConnections = val;
            return this;
        }

        public Builder maxConnectionsPerRoute(int val) {
            maxConnectionsPerRoute = val;
            return this;
        }

        public Builder ipHeaders(Set<String> val) {
            this.ipHeaders = val;
            return this;
        }

        public Builder customParametersProvider(CustomParametersProvider val) {
            this.customParametersProvider = val;
            return this;
        }

        public Builder blockHandler(BlockHandler val) {
            this.blockHandler = val;
            return this;
        }

        public Builder collectorUrl(String val) {
            this.collectorUrl = val;
            return this;
        }

        public Builder xhrFirstPartyEnabled(boolean val) {
            this.xhrFirstPartyEnabled = val;
            return this;
        }

        public Builder firstPartyEnabled(boolean val) {
            this.firstPartyEnabled = val;
            return this;
        }

        public Builder clientHost(String val) {
            this.clientHost = val;
            return this;
        }

        public Builder testingMode(boolean testingMode) {
            this.testingMode = testingMode;
            return this;
        }

        public Builder validateRequestQueueInterval(int validateRequestQueueInterval) {
            this.validateRequestQueueInterval = validateRequestQueueInterval;
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