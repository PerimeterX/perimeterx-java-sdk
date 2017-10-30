package com.perimeterx.models.activities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.utils.Constants;
import com.perimeterx.utils.JsonUtils;

import java.lang.reflect.Field;

/**
 * Created by nitzangoldfeder on 29/10/2017.
 */
public class EnforcerTelemetryActivityDetails implements ActivityDetails {

    @JsonProperty("module_version")
    private String moduleVersion;
    @JsonProperty("enforcer_configs")
    private String enforcerConfigs;

    public EnforcerTelemetryActivityDetails(PXConfiguration pxConfiguration) {
        this.moduleVersion = Constants.SDK_VERSION;
        try {
            this.enforcerConfigs = JsonUtils.writer.writeValueAsString(pxConfiguration);
        } catch (JsonProcessingException e) {
            this.enforcerConfigs = "Could not retrive pxConfiguration";
        }
    }

    public String getModuleVersion() {
        return moduleVersion;
    }
    public String getEnforcerConfigs() {
        return enforcerConfigs;
    }


}
