package com.perimeterx.api.additionalContext.credentialsIntelligence.protocol;

import com.perimeterx.api.additionalContext.credentialsIntelligence.CIProtocol;
import com.perimeterx.api.additionalContext.credentialsIntelligence.UserLoginData;
import com.perimeterx.models.configuration.credentialsIntelligenceconfig.LoginCredentials;

import static com.perimeterx.utils.HashAlgorithm.SHA256;
import static com.perimeterx.utils.StringUtils.encodeString;

public class V1CIProtocol implements CredentialsIntelligenceProtocol {

    @Override
    public UserLoginData generateUserLoginData(LoginCredentials loginCredentials) {
        return new UserLoginData(
                encodeString(loginCredentials.getPassword(), SHA256),
                encodeString(loginCredentials.getUsername(), SHA256),
                loginCredentials.getUsername(),
                CIProtocol.V1,
                null
        );
    }
}
