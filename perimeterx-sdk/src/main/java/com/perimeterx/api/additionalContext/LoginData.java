package com.perimeterx.api.additionalContext;

import com.perimeterx.api.additionalContext.credentialsIntelligence.UserLoginData;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.exceptions.PXException;
import com.perimeterx.utils.logger.IPXLogger;
import lombok.Getter;
import lombok.Setter;

import javax.servlet.http.HttpServletRequest;

@Getter
@Setter
public class LoginData {

    private UserLoginData loginCredentials;
    private Boolean loginSuccessful;
    private Integer responseStatusCode;
    private IPXLogger logger;

    public LoginData(HttpServletRequest request, PXConfiguration configuration, IPXLogger logger) throws PXException {
        setLogger(logger);
        generateLoginCredentials(request, configuration);
    }

    private void generateLoginCredentials(HttpServletRequest request, PXConfiguration configuration) throws PXException {
        this.loginCredentials = CredentialsIntelligenceManager.getUserLoginData(configuration, request, logger);
    }
}
