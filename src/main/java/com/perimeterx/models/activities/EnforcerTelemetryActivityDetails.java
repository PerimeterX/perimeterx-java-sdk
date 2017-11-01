package com.perimeterx.models.activities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.utils.Constants;
import com.perimeterx.utils.JsonUtils;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;

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

    public EnforcerTelemetryActivityDetails(PXConfiguration pxConfiguration) {
        this.moduleVersion = Constants.SDK_VERSION;
        this.osName = System.getProperty("os.name");

        try {
            this.nodeName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            this.nodeName = "unknown";
        }

        try {
            this.enforcerConfigs = JsonUtils.writer.writeValueAsString(pxConfiguration);
        } catch (JsonProcessingException e) {
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
}
