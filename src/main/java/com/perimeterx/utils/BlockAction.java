package com.perimeterx.utils;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Created by nitzangoldfederer on 29/06/2017.
 */
public enum BlockAction {
    BLOCK("block"), CAPTCHA("captcha"), CHALLENGE("challenge");

    private final String value;

    BlockAction(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return this.value;
    }

}
