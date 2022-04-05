package com.perimeterx.api.additionals2s.credentialsIntelligence;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.perimeterx.api.additionals2s.CredentialsIntelligence;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.exceptions.PXException;
import lombok.Getter;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Getter
public class AdditionalS2SContext {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UserLoginData loginCredentials;

    public AdditionalS2SContext(HttpServletRequest request, PXConfiguration configuration) throws IOException, PXException {
        generateLoginCredentials(request, configuration);
    }

    private void generateLoginCredentials(HttpServletRequest request, PXConfiguration configuration) throws IOException, PXException {
        final CredentialsIntelligence credentialsIntelligence = new CredentialsIntelligence(configuration, request);
        this.loginCredentials = credentialsIntelligence.getUserLoginData();
    }
}
