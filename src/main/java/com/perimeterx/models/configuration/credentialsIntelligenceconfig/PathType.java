package com.perimeterx.models.configuration.credentialsIntelligenceconfig;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PathType {

    @JsonProperty("regex")
    REGEX,
    @JsonProperty("exact")
    EXACT
}
