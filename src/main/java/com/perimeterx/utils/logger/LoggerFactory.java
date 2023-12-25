package com.perimeterx.utils.logger;

import com.perimeterx.models.configuration.PXConfiguration;

public class LoggerFactory {

    public IPXLogger getLogger(boolean isMemoryEnabled) {
        LoggerSeverity pxLoggerSeverity = PXConfiguration.getPxLoggerSeverity();
        if (pxLoggerSeverity == null) {
            return new Slf4JLogger(isMemoryEnabled);
        } else {
            return new ConsoleLogger(pxLoggerSeverity,isMemoryEnabled);
        }
    }
    public IPXLogger getLogger() {
        return getLogger(false);
    }
}
