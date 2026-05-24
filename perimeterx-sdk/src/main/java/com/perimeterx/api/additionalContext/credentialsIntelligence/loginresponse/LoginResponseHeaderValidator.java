package com.perimeterx.api.additionalContext.credentialsIntelligence.loginresponse;

import com.perimeterx.http.ResponseWrapper;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.utils.logger.IPXLogger;

public class LoginResponseHeaderValidator implements LoginResponseValidator {
    private final String headerName;
    private final String headerValue;
    private final IPXLogger logger;

    public LoginResponseHeaderValidator(PXConfiguration configuration, PXContext context) {
        this.headerName = configuration.getHeaderNameToValidateLoginResponse();
        this.headerValue = configuration.getHeaderValueToValidateLoginResponse();
        this.logger = context.logger;
    }

    @Override
    public boolean isSuccessfulLogin(ResponseWrapper response) {
        if (response == null || response.getHeaderNames().isEmpty()) {
            return false;
        }

        return response.getHeader(headerName).equals(headerValue);
    }
}
