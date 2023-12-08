package com.perimeterx.api.additionalContext;

import com.perimeterx.api.additionalContext.credentialsIntelligence.UserLoginData;
import com.perimeterx.api.additionalContext.credentialsIntelligence.loginrequest.CredentialsExtractor;
import com.perimeterx.api.additionalContext.credentialsIntelligence.loginrequest.CredentialsExtractorFactory;
import com.perimeterx.api.additionalContext.credentialsIntelligence.protocol.CredentialsIntelligenceProtocol;
import com.perimeterx.api.additionalContext.credentialsIntelligence.protocol.CredentialsIntelligenceProtocolFactory;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.configuration.credentialsIntelligenceconfig.CredentialsExtractionDetails;
import com.perimeterx.models.configuration.credentialsIntelligenceconfig.LoginCredentials;
import com.perimeterx.models.exceptions.PXException;
import com.perimeterx.utils.logger.IPXLogger;

import javax.servlet.http.HttpServletRequest;

public class CredentialsIntelligenceManager {

    public static UserLoginData getUserLoginData(PXConfiguration pxConfiguration, HttpServletRequest request, IPXLogger logger) throws PXException {
        if (pxConfiguration.isLoginCredentialsExtractionEnabled()) {
            final LoginCredentials credentials = getCredentials(pxConfiguration, request, logger);

            if (credentials != null && !credentials.isCredentialsEmpty()) {
                return generateUserLoginData(pxConfiguration, credentials);
            }
        }
        return null;
    }

    private static LoginCredentials getCredentials(PXConfiguration pxConfiguration, HttpServletRequest request, IPXLogger logger) {
        final CredentialsExtractionDetails credentialsExtractionDetails = getCredentialsExtractionDetails(pxConfiguration, request);

        LoginCredentials loginCredentials = pxConfiguration.getCredentialsCustomExtractor().extractCredentials(request);
        if (loginCredentials != null) {
            return loginCredentials;
        }
        else if (credentialsExtractionDetails != null) {
            return extractCredentials(request, credentialsExtractionDetails, logger);
        }

        return null;
    }

    private static CredentialsExtractionDetails getCredentialsExtractionDetails(PXConfiguration pxConfiguration, HttpServletRequest request) {
        return pxConfiguration
                .getLoginCredentialsExtractionDetails()
                .getCredentialsExtractionDetails(request.getServletPath(), request.getMethod());
    }

    private static LoginCredentials extractCredentials(HttpServletRequest request, CredentialsExtractionDetails credentialsExtractionDetails, IPXLogger logger) {
        LoginCredentials loginCredentials = null;
        final CredentialsExtractor credentialsExtractor = CredentialsExtractorFactory.create(credentialsExtractionDetails, logger);
        if (credentialsExtractor!=null) {
            loginCredentials = credentialsExtractor.extractCredentials(request);
        }
        return loginCredentials;
    }

    private static UserLoginData generateUserLoginData(PXConfiguration pxConfiguration, LoginCredentials credentials) throws PXException {
        final CredentialsIntelligenceProtocol ciProtocol = CredentialsIntelligenceProtocolFactory.create(pxConfiguration.getCiProtocol());

        return ciProtocol.generateUserLoginData(credentials);
    }
}
