package com.perimeterx.api.additionals2s.credentialsIntelligence.loginresponse;

import com.perimeterx.http.ResponseWrapper;

public interface LoginResponseValidator {
    boolean isSuccessfulLogin(ResponseWrapper response);
}
