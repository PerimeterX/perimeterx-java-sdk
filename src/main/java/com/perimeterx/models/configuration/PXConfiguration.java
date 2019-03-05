package com.perimeterx.models.configuration;

import com.perimeterx.api.blockhandler.BlockHandler;
import com.perimeterx.api.blockhandler.DefaultBlockHandler;
import com.perimeterx.api.providers.CustomParametersProvider;
import com.perimeterx.api.providers.DefaultCustomParametersProvider;
import com.perimeterx.utils.Constants;
import com.perimeterx.utils.PXLogger;
import lombok.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * PX configuration object
 * <p>
 * Created by shikloshi on 03/07/2016.
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PXConfiguration {
    private static final PXLogger logger = PXLogger.getLogger(PXConfiguration.class);

    private String appId;
    private String cookieKey;
    private String authToken;
    @Builder.Default private boolean moduleEnabled = true;
    @Builder.Default private boolean encryptionEnabled = true;
    @Builder.Default private int blockingScore = 100;
    @Builder.Default private Set<String> sensitiveHeaders = new HashSet<>(Arrays.asList("cookieOrig", "cookies"));
    @Builder.Default private int maxBufferLen = 10;
    @Builder.Default private int apiTimeout = 1000;
    @Builder.Default private int connectionTimeout = 1000;
    @Builder.Default private boolean sendPageActivities = true;
    @Builder.Default private boolean signedWithIP = false;
    private String serverURL;
    private String customLogo;
    private String cssRef;
    private String jsRef;
    @Builder.Default private Set<String> sensitiveRoutes = new HashSet<>();
    @Builder.Default private Set<String> ipHeaders = new HashSet<>();
    private String checksum;
    @Builder.Default private boolean remoteConfigurationEnabled = false;
    @Builder.Default private ModuleMode moduleMode = ModuleMode.MONITOR;
    @Builder.Default private int remoteConfigurationInterval = 1000 * 5;
    @Builder.Default private int remoteConfigurationDelay = 0;
    @Builder.Default private int maxConnections = 200;
    @Builder.Default private int maxConnectionsPerRoute = 50;
    @Builder.Default private String remoteConfigurationUrl = Constants.REMOTE_CONFIGURATION_SERVER_URL;
    @Builder.Default private CustomParametersProvider customParametersProvider = new DefaultCustomParametersProvider();;
    @Builder.Default private BlockHandler blockHandler = new DefaultBlockHandler();
    private String collectorUrl;
    @Builder.Default private String clientHost = Constants.CLIENT_HOST;
    @Builder.Default private boolean firstPartyEnabled = true;
    @Builder.Default private boolean xhrFirstPartyEnabled = true;
    private boolean useProxy;
    private String proxyHost;
    private int proxyPort;
    private boolean testingMode;
    @Builder.Default private int validateRequestQueueInterval = 5 * 1000;
    private String bypassMonitorHeader;

    /**
     * @return Configuration Object clone without cookieKey and authToken
     **/
    public PXConfiguration getTelemetryConfig() {
        return new PXConfiguration(appId, null, null, moduleEnabled, encryptionEnabled, blockingScore, sensitiveHeaders, maxBufferLen, apiTimeout,
                connectionTimeout, sendPageActivities, signedWithIP, serverURL, customLogo, cssRef, jsRef, sensitiveRoutes, ipHeaders, checksum, remoteConfigurationEnabled,
                moduleMode, remoteConfigurationInterval, remoteConfigurationDelay, maxConnections, maxConnectionsPerRoute, remoteConfigurationUrl,
                customParametersProvider, blockHandler, collectorUrl, clientHost, firstPartyEnabled, xhrFirstPartyEnabled, useProxy, proxyHost, proxyPort, testingMode,validateRequestQueueInterval, bypassMonitorHeader);
    }

    public void disableModule() {
        this.moduleEnabled = false;
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

    public static class PXConfigurationBuilder {

        public  PXConfigurationBuilder appId(String appId){
            this.appId = appId;

            if (this.serverURL == null) {
                this.serverURL = String.format(Constants.SERVER_URL, appId.toLowerCase());
            }

            if (this.collectorUrl == null) {
                this.collectorUrl = String.format(Constants.COLLECTOR_URL, appId.toLowerCase());
            }
            return this;
        }
    }
}
