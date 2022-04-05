package com.perimeterx.api.additionals2s.credentialsIntelligence.loginresponse;

import com.perimeterx.http.ResponseWrapper;
import com.perimeterx.models.configuration.PXConfiguration;

import java.util.Arrays;

public class LoginResponseStatusCodeValidator implements LoginResponseValidator {
    private final int[] statusCode;

    public LoginResponseStatusCodeValidator(PXConfiguration configuration) {
        this.statusCode = configuration.getLoginResponseValidationStatusCode();
    }

    @Override
    public boolean isSuccessfulLogin(ResponseWrapper response) {
        return Arrays.stream(statusCode).anyMatch(sc -> sc == response.getStatus());
    }
}
