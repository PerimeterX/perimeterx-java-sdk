package com.perimeterx.utils.logger;

import lombok.*;
import com.fasterxml.jackson.annotation.*;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class LogRecord {

    @JsonProperty("message") @NonNull
    private String msg;

    @JsonProperty("enforcerName") @NonNull
    private String enforcerName = "JavaSDK";

    @JsonProperty @NonNull
    private LoggerSeverity severity;

    @JsonProperty("messageTimestamp") @NonNull
    private long timestamp;

    @JsonProperty("appID")
    private String appId;

    @JsonProperty
    private final String container = "enforcer";

    @JsonProperty
    private String path;

    @JsonProperty
    private String method;

    @JsonProperty
    private String host;

    @JsonProperty
    private String requestId;

    @JsonProperty
    private String configID;

    @JsonProperty
    private String configVersion;

    public LogRecord(String msg, LoggerSeverity severity) {
        this.msg=msg;
        this.severity = severity;
        this.timestamp =new Date().getTime();
    }
}
