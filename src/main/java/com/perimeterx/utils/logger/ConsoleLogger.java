package com.perimeterx.utils.logger;

import java.io.PrintStream;
public class ConsoleLogger extends LogMemory {

    public ConsoleLogger(LoggerSeverity severity, boolean isMemoryEnabled) {
        super(isMemoryEnabled,severity);
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
    public void _debug(LogReason reason, Object... args) {
        if(severity.level >= LoggerSeverity.DEBUG.level) {
            log(System.out, IPXLogger.DEBUG_PREFIX, reason, args);
        }
    }

    public void _debug(String msg, Object... args) {
        addLog(msg, this.severity);
        if(severity.level >= LoggerSeverity.DEBUG.level) {
            log(System.out, IPXLogger.DEBUG_PREFIX, msg, args);
        }
    }

    @Override
    public void _error(LogReason reason, Object... args) {
        if(severity.level >= LoggerSeverity.ERROR.level) {
            log(System.err, IPXLogger.ERROR_PREFIX, reason, args);
        }
    }

    @Override
    public void _error(String msg, Object... args) {
        if(severity.level >= LoggerSeverity.ERROR.level) {
            log(System.err, msg, args);
        }
    }
}
