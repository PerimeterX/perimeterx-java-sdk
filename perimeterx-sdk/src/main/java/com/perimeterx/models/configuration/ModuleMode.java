package com.perimeterx.models.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nitzangoldfeder on 26/06/2017.
 */
public enum ModuleMode {
    MONITOR(0), BLOCKING(1);

    private final int value;

    private static final Map<Integer, ModuleMode> namesMap = new HashMap<>(2);
    static {
        namesMap.put(0, MONITOR);
        namesMap.put(1, BLOCKING);
    }

    @JsonCreator
    public static ModuleMode forValue(Integer value) {
        return namesMap.get(value);
    }

    @JsonValue
    public int getValue() {
        return this.value;
    }

    ModuleMode(int value) {
        this.value = value;
    }
}
