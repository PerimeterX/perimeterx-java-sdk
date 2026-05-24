package com.perimeterx.utils;

public enum HashAlgorithm {
    SHA256("SHA-256");

    private final String value;

    HashAlgorithm(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
