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

    private int value;

    private static Map<Integer, ModuleMode> namesMap = new HashMap<>(2);

    static {
        namesMap.put(0, MONITOR);
        namesMap.put(1, BLOCKING);
    }

    @JsonCreator
    public static ModuleMode forValue(Integer value) {
        return namesMap.get(value);
    }

    @JsonValue
    public Integer toValue() {
        for (Map.Entry<Integer, ModuleMode> entry : namesMap.entrySet()) {
            if (entry.getValue() == this)
                return entry.getKey();
        }
        return 0;
    }

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
