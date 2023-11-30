package com.perimeterx.utils.logger;

import com.perimeterx.utils.LoggerSeverity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Slf4JLogger implements PXLogger {

    private final Logger logger;
    private final LoggerDispatcher loggerDispatcher;

    public static PXLogger getLogger(Class<?> clazz) {
        return new Slf4JLogger(clazz);
    }
    private Slf4JLogger(Class<?> clazz) {
        logger = LoggerFactory.getLogger(clazz);
        loggerDispatcher = LoggerDispatcher.getInstance();
    }
    @Override
    public void debug(LogReason reason, Object... args) {
        loggerDispatcher.addLog(reason.toString(), LoggerSeverity.DEBUG);
        logger.debug(DEBUG_PREFIX + reason, args);
    }
    @Override
    public void debug(String msg, Object... args) {
        loggerDispatcher.addLog(msg , LoggerSeverity.DEBUG);
        logger.debug(DEBUG_PREFIX + msg, args);
    }
    @Override
    public void error(LogReason reason, Object... args) {
        loggerDispatcher.addLog(reason.toString() , LoggerSeverity.ERROR);
        logger.error(ERROR_PREFIX + reason, args);
    }

    @Override
    //TODO add function that encapsulates loggerDispatcher
    public void error(String msg, Object... args) {
        loggerDispatcher.addLog(msg , LoggerSeverity.ERROR);
        logger.error(ERROR_PREFIX + msg, args);
    }
}