package com.perimeterx.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Slf4JLogger implements PXLogger {

    private final Logger logger;

    public static PXLogger getLogger(Class<?> clazz) {
        return new Slf4JLogger(clazz);
    }
    private Slf4JLogger(Class<?> clazz) {
        logger = LoggerFactory.getLogger(clazz);
    }
    @Override
    public void debug(LogReason reason, Object... args) {
        logger.debug(DEBUG_PREFIX + reason, args);
    }
    @Override
    public void debug(String msg, Object... args) {
        logger.debug(DEBUG_PREFIX + msg, args);
    }
    @Override
    public void error(LogReason reason, Object... args) {
        logger.error(ERROR_PREFIX + reason, args);
    }

    @Override
    public void error(String msg, Object... args) {
        logger.error(ERROR_PREFIX + msg, args);
    }
}