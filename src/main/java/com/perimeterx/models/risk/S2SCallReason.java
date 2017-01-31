package com.perimeterx.models.risk;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * S2SCallReason Enum
 * <p>
 * Created by shikloshi on 05/07/2016.
 */

public enum S2SCallReason {

    NONE("none"), NO_COOKIE("no_cookie"), EXPIRED_COOKIE("cookie_expired"), INVALID_DECRYPTION("cookie_decryption_failed"), INVALID_VERIFICATION("cookie_validation_failed");

    private String value;

    S2SCallReason(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return this.value;
    }
}
