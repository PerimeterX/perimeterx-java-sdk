package com.perimeterx.api.additionalContext.credentialsIntelligence.loginresponse;

import com.perimeterx.http.ResponseWrapper;

public class DefaultCustomLoginResponseValidator implements LoginResponseValidator {

    @Override
    public boolean isSuccessfulLogin(ResponseWrapper response) {
        return false;
    }
}
