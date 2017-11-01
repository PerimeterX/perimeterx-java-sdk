package com.perimeterx.models.configuration;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Created by nitzangoldfeder on 01/11/2017.
 */
public enum CaptchaProvider {
    FUNCAPTCHA("funCaptcha"), RECAPTCHA("reCaptcha");

    String template;

    CaptchaProvider(String template) {
        this.template = template;
    }

    @JsonValue
    public String getValue() {
        return this.name();
    }
}
