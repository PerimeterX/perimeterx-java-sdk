package com.perimeterx.models.configuration.credentialsIntelligenceconfig;


import com.fasterxml.jackson.annotation.JsonProperty;

public enum LoginRequestMethod {
    @JsonProperty("post")
    POST,

    @JsonProperty("put")
    PUT,

    @JsonProperty("get")
    GET;
}
