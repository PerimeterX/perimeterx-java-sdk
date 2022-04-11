package com.perimeterx.api.additionals2s.credentialsIntelligence.protocol;

import com.perimeterx.api.additionals2s.credentialsIntelligence.CIProtocol;
import com.perimeterx.api.additionals2s.credentialsIntelligence.SSOStep;
import com.perimeterx.api.additionals2s.credentialsIntelligence.UserLoginData;
import com.perimeterx.models.configuration.credentialsIntelligenceconfig.LoginCredentials;
import com.perimeterx.utils.HashAlgorithm;

import static com.perimeterx.utils.StringUtils.encodeString;

public class MultiStepSSOCIProtocol implements CredentialsIntelligenceProtocol{
    @Override
    public UserLoginData generateUserLoginData(LoginCredentials loginCredentials) {
        final String encodedPassword = loginCredentials.getPassword() != null ?
                encodeString(loginCredentials.getPassword(), HashAlgorithm.SHA256) : null;
        final String username = loginCredentials.getUsername();
        final SSOStep ssoStep = encodedPassword != null ? SSOStep.PASSWORD : SSOStep.USERNAME;

        return new UserLoginData(encodedPassword, username, username, CIProtocol.MULTI_STEP_SSO, ssoStep);
    }
}
