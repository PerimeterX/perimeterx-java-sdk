package com.perimeterx.api.additionals2s.credentialsIntelligence;

import com.perimeterx.models.configuration.credentialsIntelligenceconfig.LoginCredentials;

public interface CredentialsIntelligenceProtocol {
    UserLoginData generateUserLoginData(LoginCredentials credentials);
}
