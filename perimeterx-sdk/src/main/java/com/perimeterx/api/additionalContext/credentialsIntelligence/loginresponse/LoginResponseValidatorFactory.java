package com.perimeterx.api.additionalContext.credentialsIntelligence.loginresponse;

import com.perimeterx.models.PXContext;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.exceptions.PXException;

import java.util.Arrays;

public class LoginResponseValidatorFactory {
    public static LoginResponseValidator create(PXConfiguration config, PXContext context) throws PXException {
        switch (config.getLoginResponseValidationReportingMethod()) {
            case BODY:
                return new LoginResponseBodyValidator(config,context);
            case HEADER:
                return new LoginResponseHeaderValidator(config,context);
            case STATUS:
                return new LoginResponseStatusCodeValidator(config,context);
            case CUSTOM:
                return new LoginResponseCustomValidator(config,context);
            case NONE:
            default:
                throw new PXException(String.format("Unknown Login Response Validation Reporting Method %s , acceptable versions are %s",
                        config.getLoginResponseValidationReportingMethod(),
                        Arrays.toString(LoginResponseValidationReportingMethod.values())));
        }
    }
}
