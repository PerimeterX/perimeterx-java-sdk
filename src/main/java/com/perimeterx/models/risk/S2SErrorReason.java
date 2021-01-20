package com.perimeterx.models.risk;

import com.fasterxml.jackson.annotation.JsonValue;

public enum S2SErrorReason {
    NO_ERROR(null),
    UNABLE_TO_SEND_REQUEST("unable_to_send_request"),
    BAD_REQUEST("bad_request"),
    SERVER_ERROR("server_error"),
    INVALID_RESPONSE("invalid_response"),
    REQUEST_FAILED_ON_SERVER("request_failed_on_server"),
    UNKNOWN_ERROR("unknown_error");

    public String value;

    S2SErrorReason(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return this.value;
    }

}
