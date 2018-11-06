package com.perimeterx.models.risk;

import com.fasterxml.jackson.annotation.JsonValue;

public enum VidSource {

    RISK_COOKIE("RISK_COOKIE"),
    VID_COOKIE("VID_COOKIE"),
    NONE("NONE");
    private String value;
    VidSource(String value){
        this.value = value;
    }
    @JsonValue
    public String getValue() {
        return this.value;
    }

}
