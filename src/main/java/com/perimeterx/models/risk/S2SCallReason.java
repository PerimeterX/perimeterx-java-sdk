package com.perimeterx.models.risk;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * S2SCallReason Enum
 * <p>
 * Created by shikloshi on 05/07/2016.
 */

public enum S2SCallReason {

    NONE("none"),
    NO_COOKIE("mobile_error_1"),
    COOKIE_EXPIRED("cookie_expired"),
    INVALID_DECRYPTION("cookie_decryption_failed"),
    INVALID_VERIFICATION("cookie_validation_failed"),
    SENSITIVE_ROUTE("sensitive_route"),
    MOBILE_SDK_CONNECTION("mobile_error_2"),
    MOBILE_SDK_PINNING("mobile_error_3"),
    MOBILE_ERROR_BYPASS("mobile_error_4");

    private String value;

    S2SCallReason(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return this.value;
    }
}
