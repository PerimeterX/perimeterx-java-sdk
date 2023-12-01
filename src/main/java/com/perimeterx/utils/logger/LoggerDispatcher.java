package com.perimeterx.utils.logger;

import com.perimeterx.api.additionalContext.credentialsIntelligence.loginrequest.RequestHeaderExtractor;
import com.perimeterx.http.*;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.exceptions.PXException;
import com.perimeterx.utils.Constants;
import com.perimeterx.utils.LoggerSeverity;
import com.perimeterx.utils.StringUtils;
import org.sonatype.plexus.components.sec.dispatcher.model.Config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.perimeterx.utils.Constants.*;

public class LoggerDispatcher {

    private static final PXLogger logger = PXLogger.getLogger(LoggerDispatcher.class);
    private static final LoggerDispatcher loggerMemory = new LoggerDispatcher();
    private final List<LogRecord> memory;
//    private final IPXHttpClient client;
    private LoggerDispatcher() {
        memory = new ArrayList<>();
    }

    public static LoggerDispatcher getInstance() {
        return loggerMemory;
    }

    public void addLog(String msg, LoggerSeverity severity) {

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
            client.sendLogs(this.memory);
        } catch (Exception e) {
            logger.error("Failed to send logs to logging service. Error :: ", e, ". Logs: ", this.memory.toString());
            throw new RuntimeException(e);
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
