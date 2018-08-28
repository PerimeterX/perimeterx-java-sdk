package com.perimeterx.models.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Created by nitzangoldfeder on 26/06/2017.
 */
public enum ModuleMode {

    MONITOR("monitor"), BLOCKING("active_blocking");

    private String value;

    ModuleMode(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public void setValue(String value) {
        this.value = value;
    }
}
