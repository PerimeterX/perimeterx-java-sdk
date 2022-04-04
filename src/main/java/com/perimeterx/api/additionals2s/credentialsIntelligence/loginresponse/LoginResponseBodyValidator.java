package com.perimeterx.api.additionals2s.credentialsIntelligence.loginresponse;

import com.perimeterx.http.ResponseWrapper;
import com.perimeterx.models.configuration.PXConfiguration;

public class LoginResponseBodyValidator implements LoginResponseValidator{
    private final String configRegexBody;

    public LoginResponseBodyValidator(PXConfiguration config) {
        this.configRegexBody = config.getLoginResponseValidationRegexBody();
    }

    @Override
    public boolean isSuccessfulLogin(ResponseWrapper response) {
        if(response == null || response.toString() == null) {
            return false;
        }

        return response.toString().matches(this.configRegexBody);
    }
}
