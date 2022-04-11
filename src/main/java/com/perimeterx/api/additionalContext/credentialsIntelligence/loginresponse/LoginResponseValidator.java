package com.perimeterx.api.additionalContext.credentialsIntelligence.loginresponse;

import com.perimeterx.http.ResponseWrapper;

public interface LoginResponseValidator {
    boolean isSuccessfulLogin(ResponseWrapper response);
}
