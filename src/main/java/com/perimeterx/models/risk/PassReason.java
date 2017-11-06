package com.perimeterx.models.risk;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Created by nitzangoldfeder on 30/05/2017.
 */
public enum PassReason {
    NONE(null),
    COOKIE("cookie"),
    CAPTCHA("captcha"),
    CAPTCHA_TIMEOUT("captcha_timeout"),
    S2S("s2s"),
    S2S_TIMEOUT("s2s_timeout"),
    ERROR("error");

    public String value;

    PassReason(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return this.value;
    }

}
