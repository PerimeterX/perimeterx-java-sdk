package com.perimeterx.utils.logger;

import com.perimeterx.models.configuration.PXConfiguration;

public class LoggerFactory {

    public IPXLogger getRequestContextLogger(boolean isMemoryEnabled) {
        LoggerSeverity pxLoggerSeverity = PXConfiguration.getPxLoggerSeverity();
        if (pxLoggerSeverity == null) {
            return new Slf4JLogger(isMemoryEnabled);
        } else {
            return new ConsoleLogger(pxLoggerSeverity,isMemoryEnabled);
        }
    }
    public IPXLogger getRequestContextLogger() {
        return getRequestContextLogger(false);
    }

    public static IPXLogger getGlobalLogger() {
        return new ConsoleLogger(LoggerSeverity.ERROR, false);
    }
}
