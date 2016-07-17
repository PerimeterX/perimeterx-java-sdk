package com.perimeterx.models.risk;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * HeaderEntry
 *
 * Created by shikloshi on 05/07/2016.
 */
public class HeaderEntry implements Map.Entry<String, String> {

    private String key;
    private String value;

    public HeaderEntry() {
    }

    public HeaderEntry(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String getKey() {
        return key;
    }

    @JsonProperty("name")
    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    @JsonProperty("value")
    public String setValue(String value) {
        String oldVal = this.value;
        this.value = value;
        return oldVal;
    }

}
