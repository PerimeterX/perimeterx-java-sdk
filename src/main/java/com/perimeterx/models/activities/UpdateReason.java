package com.perimeterx.models.activities;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Created by nitzangoldfeder on 01/11/2017.
 */
public enum UpdateReason {
    TELEMETRY("telemetry"), REMOTE_CONFIG("remote_config");

    String reason;

    UpdateReason(String reason) {
        this.reason = reason;
    }

    @JsonValue
    public String getValue() {
        return this.reason;
    }
}