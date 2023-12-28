package com.perimeterx.api.additionalContext.credentialsIntelligence.loginresponse;

import com.perimeterx.http.ResponseWrapper;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.utils.logger.IPXLogger;

import java.util.Arrays;

public class LoginResponseStatusCodeValidator implements LoginResponseValidator {
    private final int[] statusCode;
    private final IPXLogger logger;

    public LoginResponseStatusCodeValidator(PXConfiguration configuration, PXContext context) {
        this.statusCode = configuration.getLoginResponseValidationStatusCode();
        this.logger = context.logger;
    }

    @Override
    public boolean isSuccessfulLogin(ResponseWrapper response) {
        return Arrays.stream(statusCode).anyMatch(sc -> sc == response.getStatus());
    }
}
