package com.perimeterx.api.additionals2s.credentialsIntelligence.loginresponse;

import com.perimeterx.http.ResponseWrapper;
import com.perimeterx.models.configuration.PXConfiguration;

public class LoginResponseHeaderValidator implements LoginResponseValidator {
    private final String headerName;
    private final String headerValue;

    public LoginResponseHeaderValidator(PXConfiguration configuration) {
        this.headerName = configuration.getHeaderNameToValidateLoginResponse();
        this.headerValue = configuration.getHeaderValueToValidateLoginResponse();
    }

    @Override
    public boolean isSuccessfulLogin(ResponseWrapper response) {
        if (response == null || response.getHeaderNames().isEmpty()) {
            return false;
        }

        return response.getHeader(headerName).equals(headerValue);
    }
}
