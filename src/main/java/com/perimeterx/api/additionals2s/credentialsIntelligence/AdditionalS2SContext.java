package com.perimeterx.api.additionals2s.credentialsIntelligence;

import com.perimeterx.api.additionals2s.CredentialsIntelligence;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.exceptions.PXException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class AdditionalS2SContext {
    private final CredentialsIntelligence credentialsIntelligence;

    public AdditionalS2SContext(HttpServletRequest request, PXConfiguration configuration) {
        this.credentialsIntelligence = new CredentialsIntelligence(configuration, request);
    }

    public void setAdditionalContext(PXContext pxContext) throws PXException, IOException {
        final UserLoginData userLoginData = credentialsIntelligence.getUserLoginData();
        pxContext.setLoginCredentials(userLoginData);
    }
}
