package com.perimeterx.models.enforcererror;

import lombok.Data;

@Data
public class EnforcerErrorReasonInfo {
    private String errorMessage;
    private String stackTraceMessage;

    public EnforcerErrorReasonInfo() {
        setEnforcerErrorReasonInfo(null, null);
    }

    public EnforcerErrorReasonInfo(String errorMessage, String stackTraceMessage) {
        setEnforcerErrorReasonInfo(errorMessage, stackTraceMessage);
    }

    public void setEnforcerErrorReasonInfo(String errorMessage, String stackTraceMessage) {
        this.errorMessage = errorMessage;
        this.stackTraceMessage = stackTraceMessage;
    }
}