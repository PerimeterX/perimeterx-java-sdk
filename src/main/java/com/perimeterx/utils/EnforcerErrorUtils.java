package com.perimeterx.utils;

import com.perimeterx.api.PerimeterX;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.enforcererror.EnforcerErrorReasonInfo;
import com.perimeterx.models.risk.PassReason;
import com.perimeterx.utils.logger.IPXLogger;

import java.util.Arrays;
import java.util.Optional;

public class EnforcerErrorUtils {
    private static final IPXLogger logger = PerimeterX.logger;

    public static void handleEnforcerError(PXContext pxContext, String errorMessage, Exception e) {
        Optional<StackTraceElement> firstStackTraceCause = Arrays.stream((e.getStackTrace())).findFirst();
        String stackTrace = null;
        if (firstStackTraceCause.isPresent()) {
            stackTrace = "At: " + firstStackTraceCause.get();
        }

        pxContext.setPassReason(PassReason.ENFORCER_ERROR);
        pxContext.setEnforcerErrorReasonInfo(new EnforcerErrorReasonInfo(errorMessage + ":" + e, stackTrace));
        logger.error(errorMessage);
    }
}
