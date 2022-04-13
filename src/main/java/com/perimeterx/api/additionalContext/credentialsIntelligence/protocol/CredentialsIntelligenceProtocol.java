package com.perimeterx.api.additionalContext.credentialsIntelligence.protocol;

import com.perimeterx.api.additionalContext.credentialsIntelligence.UserLoginData;
import com.perimeterx.models.configuration.credentialsIntelligenceconfig.LoginCredentials;

public interface CredentialsIntelligenceProtocol {
    UserLoginData generateUserLoginData(LoginCredentials credentials);
}
