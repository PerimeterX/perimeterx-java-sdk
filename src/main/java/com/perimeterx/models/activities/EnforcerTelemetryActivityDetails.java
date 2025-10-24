package com.perimeterx.models.activities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.JsonIOException;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.utils.Constants;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

/**
 * Created by nitzangoldfeder on 29/10/2017.
 */
public class EnforcerTelemetryActivityDetails implements ActivityDetails {

    @JsonProperty("module_version")
    private String moduleVersion;
    @JsonProperty("enforcer_configs")
    private TelemetryConfiguration enforcerConfigs;
    @JsonProperty("os_name")
    private String osName;
    @JsonProperty("node_name")
    private String nodeName;
    @JsonProperty("update_reason")
    private UpdateReason updateReason;
    @JsonProperty("request_id")
    private UUID requestId;

    public EnforcerTelemetryActivityDetails(PXConfiguration pxConfiguration, PXContext context, UpdateReason updateReason) {
        this.moduleVersion = Constants.SDK_VERSION;
        this.osName = System.getProperty("os.name");
        this.updateReason = updateReason;
        this.requestId = context.getRequestId();
        try {
            this.nodeName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            this.nodeName = "unknown";
        }

        try {
            PXConfiguration config = pxConfiguration.getTelemetryConfig();
            enforcerConfigs = new TelemetryConfiguration();
            enforcerConfigs.activeConfig = config;
            enforcerConfigs.staticConfig = config;
            enforcerConfigs.remoteConfig = null; // remote config not supported
        } catch (JsonIOException e) {
            enforcerConfigs = null;
        }
    }

    public String getModuleVersion() {
        return moduleVersion;
    }

    public TelemetryConfiguration getEnforcerConfigs() {
        return enforcerConfigs;
    }

    public String getOsName() {
        return osName;
    }

    public String getNodeName() {
        return nodeName;
    }

    public UUID getRequestId() {
        return requestId;
    }
}

class TelemetryConfiguration {
    @JsonProperty("active_config")
    @JsonIgnoreProperties({
            "customParametersProvider",
            "blockHandler",
            "customLoginResponseValidator",
            "credentialsCustomExtractor",
            "customIsSensitiveRequest",
            "customParametersExtraction",
            "filterByCustomFunction",
            "loggerFactory",
            "telemetryConfig",
            "reverseProxyInstance",
            "ipxHttpClientInstance",
            "ipxhttpClientInstance",
            "IPXHttpClientInstance",
            "pxClientInstance",
            "PXClientInstance",
            "pxclientInstance",
            "httpClient",
            "pxClient",
            "pxReverseProxy"
    })
    public PXConfiguration activeConfig;
    @JsonProperty("static_config")
    @JsonIgnoreProperties({
            "customParametersProvider",
            "blockHandler",
            "customLoginResponseValidator",
            "credentialsCustomExtractor",
            "customIsSensitiveRequest",
            "customParametersExtraction",
            "filterByCustomFunction",
            "loggerFactory",
            "telemetryConfig",
            "reverseProxyInstance",
            "ipxHttpClientInstance",
            "ipxhttpClientInstance",
            "IPXHttpClientInstance",
            "pxClientInstance",
            "PXClientInstance",
            "pxclientInstance",
            "httpClient",
            "pxClient",
            "pxReverseProxy"
    })
    public PXConfiguration staticConfig;
    @JsonProperty("remote_config")
    @JsonIgnoreProperties({
            "customParametersProvider",
            "blockHandler",
            "customLoginResponseValidator",
            "credentialsCustomExtractor",
            "customIsSensitiveRequest",
            "customParametersExtraction",
            "filterByCustomFunction",
            "loggerFactory",
            "telemetryConfig",
            "reverseProxyInstance",
            "ipxHttpClientInstance",
            "ipxhttpClientInstance",
            "IPXHttpClientInstance",
            "pxClientInstance",
            "PXClientInstance",
            "pxclientInstance",
            "httpClient",
            "pxClient",
            "pxReverseProxy"
    })
    public PXConfiguration remoteConfig;
}
