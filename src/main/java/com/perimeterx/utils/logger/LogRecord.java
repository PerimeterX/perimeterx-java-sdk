package com.perimeterx.utils.logger;

import com.perimeterx.utils.LoggerSeverity;
import lombok.*;
import com.fasterxml.jackson.annotation.*;

import java.sql.Timestamp;
import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class LogRecord {

    @JsonProperty @NonNull
    private String msg;

    @JsonProperty @NonNull
    private LoggerSeverity severity;

    @JsonProperty @NonNull
    private long timestamp;

    @JsonProperty
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
