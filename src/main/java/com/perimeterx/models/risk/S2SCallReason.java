package com.perimeterx.models.risk;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
 * S2SCallReason Enum
 * <p>
 * Created by shikloshi on 05/07/2016.
 */

public enum S2SCallReason {

    NONE("none"), NO_COOKIE("no_cookie"), EXPIRED_COOKIE("expired_cookie"), INVALID_DECRYPTION("cookie_decryption_failed"), INVALID_VERIFICATION("cookie_verification_failed");

    private String value;

    S2SCallReason(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return this.value;
    }
}
