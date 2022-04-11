package com.perimeterx.api.additionalContext.credentialsIntelligence.loginresponse;

import java.util.Arrays;

public enum LoginResponseValidationReportingMethod {
    NONE(""),
    HEADER("header"),
    BODY("body"),
    STATUS("status"),
    CUSTOM("custom");


    private final String value;

    LoginResponseValidationReportingMethod(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static LoginResponseValidationReportingMethod getKeyByValue(String searchValue) {
        return Arrays.stream(values()).filter(e -> e.value.equals(searchValue))
                .findFirst()
                .get();
    }
}
