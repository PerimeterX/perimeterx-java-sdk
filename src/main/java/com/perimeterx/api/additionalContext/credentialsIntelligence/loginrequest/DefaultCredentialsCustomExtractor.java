package com.perimeterx.api.additionalContext.credentialsIntelligence.loginrequest;

import com.perimeterx.models.configuration.credentialsIntelligenceconfig.LoginCredentials;

import javax.servlet.http.HttpServletRequest;

public class DefaultCredentialsCustomExtractor implements CredentialsExtractor {

    @Override
    public LoginCredentials extractCredentials(HttpServletRequest request) {
        return null;
    }
}
