package com.perimeterx.api.additionals2s.credentialsIntelligence;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class UserLoginData {
    private final String encodedPassword;
    private final String encodedUsername;
    private final String rawUsername;
    private final CIVersion version;
    private final SSOStep ssoStep;

    public UserLoginData(String encodedPassword, String encodedUsername, String rawUsername, CIVersion version, SSOStep ssoStep) {
        this.encodedPassword = encodedPassword;
        this.encodedUsername = encodedUsername;
        this.rawUsername = rawUsername;
        this.version = version;
        this.ssoStep = ssoStep;
    }
}
