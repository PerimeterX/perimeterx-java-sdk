package com.perimeterx.api.additionals2s.credentialsIntelligence.protocol;

import com.perimeterx.api.additionals2s.credentialsIntelligence.UserLoginData;
import com.perimeterx.models.configuration.credentialsIntelligenceconfig.LoginCredentials;

public interface CredentialsIntelligenceProtocol {
    UserLoginData generateUserLoginData(LoginCredentials credentials);
}
