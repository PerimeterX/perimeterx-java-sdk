package com.perimeterx.utils.logger;

import com.perimeterx.models.PXContext;
import com.perimeterx.models.configuration.PXConfiguration;

public interface IPXLogger {
    String DEBUG_PREFIX = "[PerimeterX - DEBUG] ";
    String ERROR_PREFIX = "[PerimeterX - ERROR] ";

    void debug(LogReason reason, Object... args);

    void debug(String msg, Object... args);

    void error(LogReason reason, Object... args);

    void error(String msg, Object... args);

    void sendMemoryLogs(PXConfiguration conf, PXContext ctx);

    boolean isMemoryEmpty();
}
