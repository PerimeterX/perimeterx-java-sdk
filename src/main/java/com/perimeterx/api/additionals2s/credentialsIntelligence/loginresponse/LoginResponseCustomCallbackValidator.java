package com.perimeterx.api.additionals2s.credentialsIntelligence.loginresponse;

import com.perimeterx.http.ResponseWrapper;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.utils.PXLogger;

import javax.servlet.http.HttpServletResponse;
import java.util.function.Function;

public class LoginResponseCustomCallbackValidator implements LoginResponseValidator {
    PXLogger logger = PXLogger.getLogger(LoginResponseCustomCallbackValidator.class);

    private final Function<HttpServletResponse, Boolean> customCallback;

    public LoginResponseCustomCallbackValidator(PXConfiguration configuration) {
        this.customCallback = configuration.getLoginResponseValidationCustomCallback();
    }

    @Override
    public boolean isSuccessfulLogin(ResponseWrapper response) {
        try {
            Boolean successfulLogin = this.customCallback.apply(response);
            return successfulLogin != null ? successfulLogin : false;
        } catch (Exception e) {
            logger.error("Failed to execute custom callback while trying to validate login response " + e);
            return false;
        }
    }
}
