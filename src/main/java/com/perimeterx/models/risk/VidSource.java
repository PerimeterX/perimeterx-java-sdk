package com.perimeterx.models.risk;

import com.fasterxml.jackson.annotation.JsonValue;

public enum VidSource {

    RISK_COOKIE("risk_cookie"),
    VID_COOKIE("vid_cookie"),
    NONE("none");
    private String value;
    VidSource(String value){
        this.value = value;
    }
    @JsonValue
    public String getValue() {
        return this.value;
    }

}
