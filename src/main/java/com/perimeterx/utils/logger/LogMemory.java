package com.perimeterx.utils.logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.perimeterx.http.*;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.utils.JsonUtils;
import com.perimeterx.utils.StringUtils;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public abstract class LogMemory implements IPXLogger {

    private final boolean isMemoryEnabled;
    protected LoggerSeverity severity;
    private List<LogRecord> memory;
//    private  PXConfiguration config;
    protected LogMemory(boolean isMemoryEnabled, LoggerSeverity severity) {
        this.isMemoryEnabled = isMemoryEnabled;
        if (isMemoryEnabled) {
            this.memory = new ArrayList<>();
        }
        this.severity = severity;
    }

    public void debug(LogReason reason, Object... args) {
        addLog(reason.toString(), LoggerSeverity.DEBUG);
        this._debug(reason, args);
    }
    public void debug(String msg, Object... args) {
        addLog(msg, LoggerSeverity.DEBUG);
        this._debug(msg, args);
    }

    public void error(LogReason reason, Object... args){
        addLog(reason.toString(), LoggerSeverity.ERROR);
        this._error(reason,args);
    }

    public void error(String msg, Object... args){
        addLog(msg, LoggerSeverity.ERROR);
        this._error(msg,args);
    }


    protected abstract void _debug(LogReason reason, Object... args);
    protected abstract void _debug(String msg, Object... args);
    protected abstract void _error(LogReason reason, Object... args);
    protected abstract void _error(String msg, Object... args);

    protected void addLog(String msg, LoggerSeverity severity) {
        if (isMemoryEnabled){
            memory.add(new LogRecord(msg,severity));
        }
    }

    public void sendMemoryLogs(PXConfiguration conf, PXContext ctx){
        if (this.isMemoryEnabled){
            enrichLogs(ctx);
            dispatchLogs(conf, ctx);
        }
    }

    public boolean isMemoryEmpty(){
        return this.memory==null || this.memory.isEmpty();
    }

    private void dispatchLogs(PXConfiguration conf, PXContext ctx) {
        try {
            PXClient client = ctx.getPxConfiguration().getPxClientInstance();
            client.sendLogs(this.stringifyMemory(), ctx);
        } catch (Exception e) {
            ctx.logger.error("Failed to send logs to logging service. Error :: ", e.getMessage(), ". Logs: ", this.memory.toString());
            throw new RuntimeException(e);
        } finally {
            this.memory = new ArrayList<>();
        }
    }

    private void enrichLogs(PXContext context) {
        this.memory.forEach((logRecord -> enrichLog(logRecord,context)));
    }

    private void enrichLog(LogRecord logRecord, PXContext context) {
        logRecord.setAppId(context.getAppId());
        logRecord.setHost(context.getHostname());
        logRecord.setPath(StringUtils.getFullPathWithQueryParam(context.getRequest()));
        logRecord.setMethod(context.getHttpMethod());
        logRecord.setRequestId(context.getRequestId().toString());
    }

    private String stringifyMemory() throws JsonProcessingException {
        return JsonUtils.writer.writeValueAsString(this.memory);
    }
}
