package com.perimeterx.models.activities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
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
    private String enforcerConfigs;
    @JsonProperty("os_name")
    private String osName;
    @JsonProperty("node_name")
    private String nodeName;
    @JsonProperty("update_reason")
    private UpdateReason updateReason;
    @JsonProperty
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
            Gson gson = new Gson();
            String pxConfigJson = gson.toJson(pxConfiguration.getTelemetryConfig());
            this.enforcerConfigs = pxConfigJson;
        } catch (JsonIOException e) {
            this.enforcerConfigs = "Could not retrieve pxConfiguration";
        }
    }

    public String getModuleVersion() {
        return moduleVersion;
    }

    public String getEnforcerConfigs() {
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
