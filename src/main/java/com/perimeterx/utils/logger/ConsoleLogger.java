package com.perimeterx.utils.logger;

import com.perimeterx.utils.LoggerSeverity;

import java.io.PrintStream;
public class ConsoleLogger implements PXLogger {
    private final LoggerSeverity severity;
    private final LoggerDispatcher loggerDispatcher;

    public ConsoleLogger(LoggerSeverity severity) {

        this.severity = severity;
        this.loggerDispatcher = LoggerDispatcher.getInstance();
    }

    private void log(PrintStream out, String prefix, Object msg, Object... additional) {
        StringBuilder builder = new StringBuilder();
        builder.append(prefix);
        builder.append(msg);
        for (Object arg : additional) {
            builder.append(" ").append(arg);
        }
        out.println(builder);
    }
    @Override
    public void debug(LogReason reason, Object... args) {
        loggerDispatcher.addLog(reason.toString(), this.severity);
        if(severity.level >= LoggerSeverity.DEBUG.level) {
            log(System.out, PXLogger.DEBUG_PREFIX, reason, args);
        }
    }

    @Override
    public void debug(String msg, Object... args) {
        loggerDispatcher.addLog(msg, this.severity);
        if(severity.level >= LoggerSeverity.DEBUG.level) {
            log(System.out, PXLogger.DEBUG_PREFIX, msg, args);
        }
    }

    @Override
    public void error(LogReason reason, Object... args) {
        loggerDispatcher.addLog(reason.toString(), this.severity);
        if(severity.level >= LoggerSeverity.ERROR.level) {
            log(System.err, PXLogger.ERROR_PREFIX, reason, args);
        }
    }

    @Override
    public void error(String msg, Object... args) {
        loggerDispatcher.addLog(msg, this.severity);
        if(severity.level >= LoggerSeverity.ERROR.level) {
            log(System.err, msg, args);
        }
    }
}
