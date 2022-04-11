package com.perimeterx.api.additionalContext.credentialsIntelligence.loginresponse;

import com.perimeterx.http.ResponseWrapper;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.utils.PXLogger;

public class LoginResponseCustomValidator implements LoginResponseValidator {
    final PXLogger logger = PXLogger.getLogger(LoginResponseCustomValidator.class);

    private final LoginResponseValidator customValidator;

    public LoginResponseCustomValidator(PXConfiguration configuration) {
        this.customValidator = configuration.getCustomLoginResponseValidator();
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
