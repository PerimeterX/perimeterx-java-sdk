package com.perimeterx.api.additionalContext.credentialsIntelligence.protocol;

import com.perimeterx.api.additionalContext.credentialsIntelligence.CIProtocol;
import com.perimeterx.api.additionalContext.credentialsIntelligence.UserLoginData;
import com.perimeterx.models.configuration.credentialsIntelligenceconfig.LoginCredentials;
import com.perimeterx.utils.HashAlgorithm;

import static com.perimeterx.utils.HashAlgorithm.SHA256;
import static com.perimeterx.utils.StringUtils.encodeString;
import static com.perimeterx.utils.StringUtils.isValid;

public class V2CIProtocol implements CredentialsIntelligenceProtocol {
    private final static String GMAIL_DOMAIN = "@gmail.com";

    @Override
    public UserLoginData generateUserLoginData(LoginCredentials credentials) {
        final String rawUsername = credentials.getUsername();
        final String normalizedUsername = isValid(rawUsername) ?
                getV2NormalizedEmailAddress(rawUsername) : rawUsername;
        final String encodedUsername = encodeString(normalizedUsername, HashAlgorithm.SHA256);

        return new UserLoginData(
                getEncodedV2Password(encodedUsername, credentials.getPassword()),
                encodedUsername,
                rawUsername,
                CIProtocol.V2,
                null
        );
    }

    private String getV2NormalizedEmailAddress(String emailAddress) {
        final String lowercaseAddress = emailAddress.toLowerCase();
        final int index = lowercaseAddress.indexOf('@');
        final String domain = lowercaseAddress.substring(index);

        String username = lowercaseAddress.substring(0,index);
        final int plusIndex = username.indexOf("+");
        username = plusIndex != -1 ? username.substring(0, plusIndex) : username;

        if (domain.equals(GMAIL_DOMAIN)) {
            username = username.replace(".", "");
        }

        return username + domain;
    }

    private String getEncodedV2Password(String encodedUserName, String password) {
        final String encodedPassword = encodeString(password, SHA256);

        return encodeString(encodedUserName + encodedPassword, SHA256);
    }
}
