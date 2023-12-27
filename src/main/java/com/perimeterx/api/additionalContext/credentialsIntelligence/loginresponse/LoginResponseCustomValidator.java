package com.perimeterx.api.additionalContext.credentialsIntelligence.loginresponse;

import com.perimeterx.api.PerimeterX;
import com.perimeterx.http.ResponseWrapper;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.utils.logger.IPXLogger;

public class LoginResponseCustomValidator implements LoginResponseValidator {
    private IPXLogger logger;

    private final LoginResponseValidator customValidator;

    public LoginResponseCustomValidator(PXConfiguration configuration, PXContext context) {
        this.customValidator = configuration.getCustomLoginResponseValidator();
        this.logger = context.logger;
    }

    @Override
    public boolean isSuccessfulLogin(ResponseWrapper response) {
        try {
            return this.customValidator.isSuccessfulLogin(response);
        } catch (Exception e) {
            logger.error("Failed to execute custom callback while trying to validate login response " + e);
            return false;
        }
    }
}
