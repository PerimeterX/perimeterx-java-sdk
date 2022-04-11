package com.perimeterx.api.additionals2s.credentialsIntelligence;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

public enum CIProtocol {

    @JsonProperty("v1")
    V1("v1"),

    @JsonProperty("v2")
    V2("v2"),

    @JsonProperty("multistep_sso")
    MULTI_STEP_SSO("multistep_sso"),
    ;

    private final String value;

    CIProtocol(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static CIProtocol getKeyByValue(String searchValue) {
        return Arrays.stream(values()).filter(e -> e.value.equals(searchValue))
                .findFirst()
                .get();
    }
}
