package com.perimeterx.api.additionals2s.credentialsIntelligence.loginrequest;

import com.perimeterx.models.configuration.credentialsIntelligenceconfig.LoginCredentials;

import javax.servlet.http.HttpServletRequest;

public interface CredentialsExtractor {
    LoginCredentials extractCredentials(HttpServletRequest request);
}
