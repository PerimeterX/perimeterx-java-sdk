package com.perimeterx.utils;

import com.perimeterx.api.PerimeterX;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.risk.PassReason;

import java.util.Arrays;
import java.util.Optional;

public class EnforcerErrorUtils {
    private static final PXLogger logger = PXLogger.getLogger(PerimeterX.class);

    public static void handleEnforcerError(PXContext pxContext, String errorMessage, Exception e) {
        Optional<StackTraceElement> firstStackTraceCause = Arrays.stream((e.getStackTrace())).findFirst();
        if (firstStackTraceCause.isPresent()) {
            errorMessage += ". At: " + firstStackTraceCause.get().toString();
        }

        pxContext.setPassReason(PassReason.ENFORCER_ERROR);
        pxContext.setEnforcerErrorReasonInfo(errorMessage);
        logger.error(errorMessage);
    }
}
