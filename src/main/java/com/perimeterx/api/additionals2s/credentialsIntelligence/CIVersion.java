package com.perimeterx.api.additionals2s.credentialsIntelligence;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

public enum CIVersion {

    @JsonProperty("v1")
    V1("v1"),

    @JsonProperty("multistep_sso")
    MULTI_STEP_SSO("multistep_sso"),
    ;

    private final String value;

    CIVersion(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static CIVersion getKeyByValue(String searchValue) {
        return Arrays.stream(values()).filter(e -> e.value.equals(searchValue))
                .findFirst()
                .get();
    }
}
