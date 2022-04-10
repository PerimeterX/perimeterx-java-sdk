package com.perimeterx.api.additionals2s.credentialsIntelligence.loginresponse;

import com.perimeterx.http.ResponseWrapper;

public class DefaultCustomLoginResponseValidator implements LoginResponseValidator {

    @Override
    public boolean isSuccessfulLogin(ResponseWrapper response) {
        return false;
    }
}
