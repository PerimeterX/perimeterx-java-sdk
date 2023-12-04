package com.perimeterx.utils.logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.perimeterx.api.PerimeterX;
import com.perimeterx.http.*;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.utils.JsonUtils;
import com.perimeterx.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.perimeterx.utils.Constants.*;

public abstract class LogMemory implements IPXLogger {

    private List<LogRecord> memory;
//    private  PXConfiguration config;
    protected LogMemory() {
        this.memory = new ArrayList<>();
    }

    public abstract void debug(LogReason reason, Object... args);

    public abstract void debug(String msg, Object... args);

    public abstract void error(LogReason reason, Object... args);

    public abstract void error(String msg, Object... args);

    private String stringifyMemory() throws JsonProcessingException {
        return JsonUtils.writer.writeValueAsString(this.memory);
//        StringBuilder builder = new StringBuilder();
//        builder.append('[');
//        this.memory.forEach(logRecord -> {
//            builder.append(logRecord.toString());
//        });

    }


    public static IPXLogger getLogger(String name) {
        LoggerSeverity pxLoggerSeverity = PXConfiguration.getPxLoggerSeverity();
        if (pxLoggerSeverity == null) {
            return new Slf4JLogMemory(name);
        } else {
            return new ConsoleLogMemory(pxLoggerSeverity);
        }
    }

    protected void addLog(String msg, LoggerSeverity severity) {
        memory.add(new LogRecord(msg,severity));
    }

    public void sendMemoryLogs(PXConfiguration conf, PXContext ctx){
        if (ctx != null){
            String loggerAuthToken = ctx.getHeaders().get(LOGGER_TOKEN_HEADER_NAME);
            if (conf.getLoggerAuthToken()!=null && conf.getLoggerAuthToken().equals(loggerAuthToken)) {
                enrichLogs(ctx);
                dispatchLogs(conf);
            }
        }
    }

    private void dispatchLogs(PXConfiguration conf) {
        try {
            PXClient client = conf.getPxClientInstance();
            client.sendLogs(this.stringifyMemory());
        } catch (Exception e) {
            PerimeterX.logger.error("Failed to send logs to logging service. Error :: ", e, ". Logs: ", this.memory.toString());
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


}
