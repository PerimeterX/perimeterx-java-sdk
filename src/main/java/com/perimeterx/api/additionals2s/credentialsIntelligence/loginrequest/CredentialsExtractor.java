package com.perimeterx.api.additionals2s.credentialsIntelligence.loginrequest;

import com.perimeterx.models.configuration.credentialsIntelligenceconfig.LoginCredentials;

public interface CredentialsExtractor {
    LoginCredentials extractCredentials();
}
