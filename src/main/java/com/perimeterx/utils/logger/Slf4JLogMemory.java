package com.perimeterx.utils.logger;

import org.slf4j.LoggerFactory;

public class Slf4JLogMemory extends LogMemory {

    protected org.slf4j.Logger logger;
    public Slf4JLogMemory(String name) {
        super();
        logger = LoggerFactory.getLogger(name);
    }


    @Override
    public void debug(LogReason reason, Object... args) {
        addLog(reason.toString(), LoggerSeverity.DEBUG);
        logger.debug(DEBUG_PREFIX + reason, args);
    }
    @Override
    public void debug(String msg, Object... args) {
        addLog(msg , LoggerSeverity.DEBUG);
        logger.debug(DEBUG_PREFIX + msg, args);
    }
    @Override
    public void error(LogReason reason, Object... args) {
        addLog(reason.toString() , LoggerSeverity.ERROR);
        logger.error(ERROR_PREFIX + reason, args);
    }

    @Override
    //TODO add function that encapsulates loggerDispatcher
    public void error(String msg, Object... args) {
        addLog(msg , LoggerSeverity.ERROR);
        logger.error(ERROR_PREFIX + msg, args);
    }
}