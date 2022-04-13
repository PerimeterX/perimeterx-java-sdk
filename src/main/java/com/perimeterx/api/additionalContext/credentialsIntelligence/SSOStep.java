package com.perimeterx.api.additionalContext.credentialsIntelligence;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum SSOStep {
    @JsonProperty("user")
    USERNAME,

    @JsonProperty("pass")
    PASSWORD,
}
