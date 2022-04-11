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

import javax.servlet.http.HttpServletRequest;

public class CredentialsIntelligenceManager {

    public static UserLoginData getUserLoginData(PXConfiguration pxConfiguration, HttpServletRequest request) throws PXException {
        if (pxConfiguration.isLoginCredentialsExtractionEnabled()) {
            final LoginCredentials credentials = getCredentials(pxConfiguration, request);

            if (credentials != null && !credentials.isCredentialEmpty()) {
                return generateUserLoginData(pxConfiguration, credentials);
            }
        }
        return null;
    }

    private static LoginCredentials getCredentials(PXConfiguration pxConfiguration, HttpServletRequest request) {
        final CredentialsExtractionDetails credentialsExtractionDetails = getCredentialsExtractionDetails(pxConfiguration, request);

        if (pxConfiguration.getCredentialsCustomExtractor().extractCredentials(request) != null) {

            return pxConfiguration.getCredentialsCustomExtractor().extractCredentials(request);
        }
        else if (credentialsExtractionDetails != null) {
            return extractCredentials(request, credentialsExtractionDetails);
        }

        return null;
    }

    private static CredentialsExtractionDetails getCredentialsExtractionDetails(PXConfiguration pxConfiguration, HttpServletRequest request) {
        return pxConfiguration
                .getLoginCredentialsExtractionDetails()
                .getCredentialsExtractionDetails(request.getServletPath(), request.getMethod());
    }

    private static LoginCredentials extractCredentials(HttpServletRequest request, CredentialsExtractionDetails credentialsExtractionDetails) {
        final CredentialsExtractor credentialsExtractor = CredentialsExtractorFactory.create(credentialsExtractionDetails);

        return credentialsExtractor.extractCredentials(request);

    }

    private static UserLoginData generateUserLoginData(PXConfiguration pxConfiguration, LoginCredentials credentials) throws PXException {
        final CredentialsIntelligenceProtocol ciProtocol = CredentialsIntelligenceProtocolFactory.create(pxConfiguration.getCiProtocol());

        return ciProtocol.generateUserLoginData(credentials);
    }
}
