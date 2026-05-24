package com.perimeterx.api.additionalContext;

public enum PXHDSource {
    COOKIE("cookie"),
    RISK("risk");

    private final String value;

    PXHDSource(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
