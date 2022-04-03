package com.perimeterx.api.additionals2s.credentialsIntelligence;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class UserLoginData {
    private final String encodedPassword;
    private final String username;
    private final String rawUsername;
    private final CIVersion ciVersion;
    private final SSOStep ssoStep;

    public UserLoginData(String encodedPassword, String username, String rawUsername, CIVersion version, SSOStep ssoStep) {
        this.encodedPassword = encodedPassword;
        this.username = username;
        this.rawUsername = rawUsername;
        this.ciVersion = version;
        this.ssoStep = ssoStep;
    }
}
