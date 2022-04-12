package com.perimeterx.models.configuration.credentialsIntelligenceconfig;


import com.fasterxml.jackson.annotation.JsonProperty;

public enum CredentialsLocationInRequest {

    @JsonProperty("body")
    BODY,

    @JsonProperty("query-param")
    QUERY_PARAM,

    @JsonProperty("header")
    HEADER;

}
