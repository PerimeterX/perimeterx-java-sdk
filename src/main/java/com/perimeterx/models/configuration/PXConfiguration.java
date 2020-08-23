package com.perimeterx.models.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.perimeterx.api.blockhandler.BlockHandler;
import com.perimeterx.api.blockhandler.DefaultBlockHandler;
import com.perimeterx.api.providers.CustomParametersProvider;
import com.perimeterx.api.providers.DefaultCustomParametersProvider;
import com.perimeterx.utils.Constants;
import com.perimeterx.utils.FilesUtils;
import com.perimeterx.utils.PXLogger;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
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

    @JsonProperty("px_app_id")
    private String appId;
    @JsonProperty("px_cookie_secret")
    private String cookieKey;
    @JsonProperty("px_auth_token")
    private String authToken;
    @Builder.Default
    @JsonProperty("px_enabled")
    private boolean moduleEnabled = true;
    @Builder.Default
    @JsonProperty("px_encryption_enabled")
    private boolean encryptionEnabled = true;
    @Builder.Default
    @JsonProperty("px_blocking_score")
    private int blockingScore = 100;
    @Builder.Default
    @JsonProperty("px_sensitive_headers")
    private Set<String> sensitiveHeaders = new HashSet<>(Arrays.asList("cookieOrig", "cookies"));
    @Builder.Default
    @JsonProperty("px_max_buffer_length")
    private int maxBufferLen = 20;
    @Builder.Default
    @JsonProperty("px_sync_request_timeout_ms")
    private int apiTimeout = 1000;
    @Builder.Default
    @JsonProperty("px_connection_timeout_ms")
    private int connectionTimeout = 1000;
    @Builder.Default
    @JsonProperty("px_send_async_activities")
    private boolean sendPageActivities = true;
    @Builder.Default
    private boolean signedWithIP = false;
    @JsonProperty("px_server_url")
    private String serverURL;
    @JsonProperty("px_custom_logo")
    private String customLogo;
    @JsonProperty("px_css_ref")
    private String cssRef;
    @JsonProperty("px_js_ref")
    private String jsRef;
    @Builder.Default
    @JsonProperty("px_sensitive_routes")
    @Deprecated
    private Set<String> sensitiveRoutes = new HashSet<>();
    @Builder.Default
    @JsonProperty("px_sensitive_routes_regex")
    private Set<String> sensitiveRoutesRegex = new HashSet<>();
    @Builder.Default
    @JsonProperty("px_ip_headers")
    private Set<String> ipHeaders = new HashSet<>();
    @JsonProperty("px_checksum")
    private String checksum;
    @Builder.Default
    @JsonProperty("px_remote_configuration_enabled")
    private boolean remoteConfigurationEnabled = false;
    @Builder.Default
    @JsonProperty("px_module_mode")
    private ModuleMode moduleMode = ModuleMode.MONITOR;
    @Builder.Default
    @JsonProperty("px_remote_configuration_interval_ms")
    private int remoteConfigurationInterval = 1000 * 5;
    @Builder.Default
    @JsonProperty("px_remote_configuration_delay_ms")
    private int remoteConfigurationDelay = 0;
    @Builder.Default
    @JsonProperty("px_max_http_client_connections")
    private int maxConnections = 200;
    @Builder.Default
    @JsonProperty("px_max_connections_per_route")
    private int maxConnectionsPerRoute = 50;
    @Builder.Default
    @JsonProperty("px_remote_configuration_url")
    private String remoteConfigurationUrl = Constants.REMOTE_CONFIGURATION_SERVER_URL;
    @Builder.Default
    private CustomParametersProvider customParametersProvider = new DefaultCustomParametersProvider();
    @Builder.Default
    private BlockHandler blockHandler = new DefaultBlockHandler();
    @JsonProperty("px_collector_url")
    private String collectorUrl;
    @Builder.Default
    @JsonProperty("px_client_url")
    private String clientHost = Constants.CLIENT_HOST;
    @Builder.Default
    @JsonProperty("px_first_party_enabled")
    private boolean firstPartyEnabled = true;
    @Builder.Default
    private boolean xhrFirstPartyEnabled = true;
    @JsonProperty("px_use_proxy")
    private boolean useProxy;
    @JsonProperty("px_proxy_url")
    private String proxyHost;
    @JsonProperty("px_proxy_port")
    private int proxyPort;
    @JsonProperty("px_test_mode")
    private boolean testingMode;
    @Builder.Default
    private int validateRequestQueueInterval = 5 * 1000;
    @JsonProperty("px_bypass_monitor_header")
    private String bypassMonitorHeader;
    private String configFilePath;
    @Builder.Default
    @JsonProperty("px_advanced_blocking_response")
    private boolean advancedBlockingResponse = true;

    private static final String[] extensions = {"css", "bmp", "tif", "ttf", "woff2", "docx",
            "js", "pict", "tiff", "eot", "xlsx", "jpg", "csv", "woff", "xls", "jpeg", "doc", "eps",
            "ejs", "otf", "pptx", "gif", "pdf", "swf", "svg", "ps", "ico", "pls", "midi", "svgz",
            "class", "png", "ppt", "mid", "webp", "jar", "json"};

    @Builder.Default
    private Set<String> staticFilesExt = new HashSet<>(Arrays.asList(extensions));

    /**
     * @return Configuration Object clone without cookieKey and authToken
     **/
    public PXConfiguration getTelemetryConfig() {
        return new PXConfiguration(appId, null, null, moduleEnabled, encryptionEnabled,
                blockingScore, sensitiveHeaders, maxBufferLen, apiTimeout, connectionTimeout, sendPageActivities,
                signedWithIP, serverURL, customLogo, cssRef, jsRef, sensitiveRoutes, sensitiveRoutesRegex, ipHeaders, checksum,
                remoteConfigurationEnabled, moduleMode, remoteConfigurationInterval, remoteConfigurationDelay,
                maxConnections, maxConnectionsPerRoute, remoteConfigurationUrl, customParametersProvider, blockHandler,
                collectorUrl, clientHost, firstPartyEnabled, xhrFirstPartyEnabled, useProxy, proxyHost, proxyPort,
                testingMode, validateRequestQueueInterval, bypassMonitorHeader, configFilePath, advancedBlockingResponse,
                staticFilesExt);
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

    public void mergeConfigurations() {
        String filepath = this.getConfigFilePath();
        if (!StringUtils.isEmpty(filepath)) {
            try {
                FilesUtils.readFileConfigAsPXConfig(this, filepath);
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
    }

    private void updateWithParamsMap(Map<String, String> fileConfigParams, PXConfiguration loadedConfig) {
        for (String param : fileConfigParams.keySet()) {
            try {
                Field field = this.getClass().getDeclaredField(param);
                field.setAccessible(true);
                Object newVal = field.get(loadedConfig);
                field.set(this, newVal);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                logger.error("Config param " + param + "does not exist in PXConfiguration.");
            }

        }
    }

    public static class PXConfigurationBuilder {

        public PXConfigurationBuilder appId(String appId) {
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

    /**
     * @param path the path to check against the white list extension
     * @return true if path is to static file defined at the white list
     */
    public boolean isExtWhiteListed(String path) {
        return  staticFilesExt.contains(FilenameUtils.getExtension(path));
    }
}
