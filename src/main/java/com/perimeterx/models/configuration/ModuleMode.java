package com.perimeterx.models.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Created by nitzangoldfeder on 26/06/2017.
 */
public enum ModuleMode {

    MONITOR(0), BLOCKING(1);

    private int value;

    ModuleMode(int value) {
        this.value = value;
    }


    @JsonValue
    public int getValue() {
        return this.value;
    }

    @JsonCreator
    public void setValue(int value) {
        this.value = value;
    }
}
