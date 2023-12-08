package com.perimeterx.utils.logger;

import com.perimeterx.models.configuration.PXConfiguration;

public class LoggerFactory {

    public static IPXLogger getLogger(boolean isMemoryEnabled) {
        LoggerSeverity pxLoggerSeverity = PXConfiguration.getPxLoggerSeverity();
        if (pxLoggerSeverity == null) {
            return new Slf4JLogger(isMemoryEnabled);
        } else {
            return new ConsoleLogger(pxLoggerSeverity,isMemoryEnabled);
        }
    }
    public static IPXLogger getLogger() {
        LoggerSeverity pxLoggerSeverity = PXConfiguration.getPxLoggerSeverity();
        if (pxLoggerSeverity == null) {
            return new Slf4JLogger(false);
        } else {
            return new ConsoleLogger(pxLoggerSeverity,false);
        }
    }
}
