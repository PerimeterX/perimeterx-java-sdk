package com.perimeterx.utils.logger;

import org.slf4j.LoggerFactory;

public class Slf4JLogger extends LogMemory {

    protected org.slf4j.Logger logger;
    public Slf4JLogger(boolean isMemoryEnabled) {
        super(isMemoryEnabled, LoggerSeverity.ERROR);
        logger = LoggerFactory.getLogger("PerimeterX");
        if (logger.isDebugEnabled()) {
            severity = LoggerSeverity.DEBUG;
        }
    }


    @Override
    public void _debug(LogReason reason, Object... args) {
        addLog(reason.toString(), LoggerSeverity.DEBUG);
        logger.debug(DEBUG_PREFIX + reason, args);
    }
    @Override
    public void _debug(String msg, Object... args) {
        addLog(msg , LoggerSeverity.DEBUG);
        logger.debug(DEBUG_PREFIX + msg, args);
    }
    @Override
    public void _error(LogReason reason, Object... args) {
        addLog(reason.toString() , LoggerSeverity.ERROR);
        logger.error(ERROR_PREFIX + reason, args);
    }

    @Override
    public void _error(String msg, Object... args) {
        addLog(msg , LoggerSeverity.ERROR);
        logger.error(ERROR_PREFIX + msg, args);
    }
}