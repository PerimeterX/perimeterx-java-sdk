package com.perimeterx.api.additionals2s.credentialsIntelligence.protocol;

import com.perimeterx.api.additionals2s.credentialsIntelligence.CIVersion;
import com.perimeterx.api.additionals2s.credentialsIntelligence.UserLoginData;
import com.perimeterx.models.configuration.credentialsIntelligenceconfig.LoginCredentials;
import com.perimeterx.utils.HashAlgorithm;

import java.util.Locale;

import static com.perimeterx.utils.StringUtils.*;

public class V2CIProtocol implements CredentialsIntelligenceProtocol {
    @Override
    public UserLoginData generateUserLoginData(LoginCredentials credentials) {
        final String rawUsername = credentials.getUsername();
        final String normalizedUsername = isValid(rawUsername) ?
                getV2NormalizedEmailAddress(rawUsername) :
                rawUsername.toLowerCase(Locale.ROOT);

        return new UserLoginData(
                getEncodedV2Password(normalizedUsername, credentials.getPassword()),
                encodeString(normalizedUsername, HashAlgorithm.SHA256),
                rawUsername,
                CIVersion.V2,
                null
        );
    }
}
