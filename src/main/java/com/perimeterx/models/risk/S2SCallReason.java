package com.perimeterx.models.risk;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * S2SCallReason Enum
 * <p>
 * Created by shikloshi on 05/07/2016.
 */

public enum S2SCallReason {

    NONE("none"),
    NO_COOKIE("no_cookie"),
    COOKIE_EXPIRED("cookie_expired"),
    INVALID_DECRYPTION("cookie_decryption_failed"),
    INVALID_VERIFICATION("cookie_validation_failed"),
    SENSITIVE_ROUTE("sensitive_route"),
    MOBILE_SDK_CONNECTION("mobile_sdk_connection_error"),
    MOBILE_SDK_PINNING("mobile_sdk_pinning_error");

    private String value;

    S2SCallReason(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    @JsonValue
    public String getValue() {
        return this.value;
    }
}
