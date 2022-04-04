package com.perimeterx.api.additionals2s.credentialsIntelligence.loginresponse;

import com.perimeterx.models.configuration.PXConfiguration;

public class LoginResponseValidatorFactory {
    public static LoginResponseValidator create(PXConfiguration config) {
        switch (config.getLoginResponseValidationReportingMethod()) {
            case BODY:
                return new LoginResponseBodyValidator(config);
            case HEADER:
                return new LoginResponseHeaderValidator(config);
            case STATUS:
                return new LoginResponseStatusCodeValidator(config);
            case CUSTOM:
                return new LoginResponseCustomCallbackValidator(config);
            case NONE:
            default:
                return null;
        }
    }
}
