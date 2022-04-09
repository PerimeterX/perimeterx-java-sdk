package com.perimeterx.api.additionals2s.credentialsIntelligence;

import com.perimeterx.api.additionals2s.CredentialsIntelligenceManager;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.exceptions.PXException;
import lombok.Getter;
import lombok.Setter;

import javax.servlet.http.HttpServletRequest;

@Getter
@Setter
public class AdditionalS2SContext {

    private UserLoginData loginCredentials;
    private Boolean loginSuccessful;
    private Integer responseStatusCode;

    public AdditionalS2SContext(HttpServletRequest request, PXConfiguration configuration) throws PXException {
        generateLoginCredentials(request, configuration);
    }

    private void generateLoginCredentials(HttpServletRequest request, PXConfiguration configuration) throws PXException {
        final CredentialsIntelligenceManager credentialsIntelligenceManager = new CredentialsIntelligenceManager(configuration, request);
        this.loginCredentials = credentialsIntelligenceManager.getUserLoginData();
    }
}
