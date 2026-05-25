package com.perimeterx.api.additionalContext.credentialsIntelligence;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class UserLoginData {
    private final String encodedPassword;
    private final String username;
    private final String rawUsername;
    private final CIProtocol ciProtocol;
    private final SSOStep ssoStep;

    public UserLoginData(String encodedPassword, String username, String rawUsername, CIProtocol version, SSOStep ssoStep) {
        this.encodedPassword = encodedPassword;
        this.username = username;
        this.rawUsername = rawUsername;
        this.ciProtocol = version;
        this.ssoStep = ssoStep;
    }
}
