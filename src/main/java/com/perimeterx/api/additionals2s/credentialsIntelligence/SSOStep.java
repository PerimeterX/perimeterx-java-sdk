package com.perimeterx.api.additionals2s.credentialsIntelligence;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum SSOStep {
    @JsonProperty("user")
    USERNAME,
    @JsonProperty("pass")
    PASSWORD,
}
