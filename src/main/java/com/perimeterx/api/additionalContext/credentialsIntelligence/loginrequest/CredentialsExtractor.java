package com.perimeterx.api.additionalContext.credentialsIntelligence.loginrequest;

import com.perimeterx.models.configuration.credentialsIntelligenceconfig.LoginCredentials;

import javax.servlet.http.HttpServletRequest;

public interface CredentialsExtractor {
    LoginCredentials extractCredentials(HttpServletRequest request);
}
