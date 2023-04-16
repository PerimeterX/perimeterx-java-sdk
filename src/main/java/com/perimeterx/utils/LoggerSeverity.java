package com.perimeterx.utils;

import com.fasterxml.jackson.annotation.JsonValue;

public enum LoggerSeverity {
    NONE(0),
    ERROR(10),
    DEBUG(100);


    public final int level;

    LoggerSeverity(int level) {
        this.level = level;
    }

    @JsonValue
    public String jsonName() {
        return this.name().toLowerCase();
    }
}
