package com.perimeterx.api.additionals2s.credentialsIntelligence;

import com.perimeterx.models.configuration.credentialsIntelligenceconfig.LoginCredentials;
import com.perimeterx.utils.HashAlgorithm;

import static com.perimeterx.utils.StringUtils.encodeString;

public class V1CIProtocol implements CredentialsIntelligenceProtocol {

    @Override
    public UserLoginData generateUserLoginData(LoginCredentials loginCredentials) {
        return new UserLoginData(
                encodeString(loginCredentials.getPassword(), HashAlgorithm.SHA256),
                encodeString(loginCredentials.getUsername(), HashAlgorithm.SHA256),
                loginCredentials.getUsername(),
                CIVersion.V1,
                null
        );
    }
}
