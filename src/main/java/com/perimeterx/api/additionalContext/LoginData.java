package com.perimeterx.api.additionalContext;

import com.perimeterx.api.additionalContext.credentialsIntelligence.UserLoginData;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.exceptions.PXException;
import lombok.Getter;
import lombok.Setter;

import javax.servlet.http.HttpServletRequest;

@Getter
@Setter
public class LoginData {

    private UserLoginData loginCredentials;
    private Boolean loginSuccessful;
    private Integer responseStatusCode;

    public LoginData(HttpServletRequest request, PXConfiguration configuration) throws PXException {
        generateLoginCredentials(request, configuration);
    }

    private void generateLoginCredentials(HttpServletRequest request, PXConfiguration configuration) throws PXException {
        this.loginCredentials = CredentialsIntelligenceManager.getUserLoginData(configuration, request);
    }
}
