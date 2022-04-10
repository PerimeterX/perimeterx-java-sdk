package com.perimeterx.api.additionals2s.credentialsIntelligence.loginresponse;

import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.exceptions.PXException;

import java.util.Arrays;

public class LoginResponseValidatorFactory {
    public static LoginResponseValidator create(PXConfiguration config) throws PXException {
        switch (config.getLoginResponseValidationReportingMethod()) {
            case BODY:
                return new LoginResponseBodyValidator(config);
            case HEADER:
                return new LoginResponseHeaderValidator(config);
            case STATUS:
                return new LoginResponseStatusCodeValidator(config);
            case CUSTOM:
                return new LoginResponseCustomValidator(config);
            case NONE:
            default:
                throw new PXException(String.format("Unknown Login Response Validation Reporting Method %s , acceptable versions are %s",
                        config.getLoginResponseValidationReportingMethod(),
                        Arrays.toString(LoginResponseValidationReportingMethod.values())));
        }
    }
}
