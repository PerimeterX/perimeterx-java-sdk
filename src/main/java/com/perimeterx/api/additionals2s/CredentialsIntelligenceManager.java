package com.perimeterx.api.additionals2s;

import com.perimeterx.api.additionals2s.credentialsIntelligence.UserLoginData;
import com.perimeterx.api.additionals2s.credentialsIntelligence.loginrequest.CredentialsExtractor;
import com.perimeterx.api.additionals2s.credentialsIntelligence.loginrequest.CredentialsExtractorFactory;
import com.perimeterx.api.additionals2s.credentialsIntelligence.protocol.CredentialsIntelligenceProtocol;
import com.perimeterx.api.additionals2s.credentialsIntelligence.protocol.CredentialsIntelligenceProtocolFactory;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.configuration.credentialsIntelligenceconfig.CredentialsExtractionDetails;
import com.perimeterx.models.configuration.credentialsIntelligenceconfig.LoginCredentials;
import com.perimeterx.models.exceptions.PXException;

import javax.servlet.http.HttpServletRequest;
import java.util.function.Function;

public class CredentialsIntelligenceManager {
    private final PXConfiguration pxConfiguration;
    private final HttpServletRequest request;

    public CredentialsIntelligenceManager(PXConfiguration pxConfiguration, HttpServletRequest request) {
        this.pxConfiguration = pxConfiguration;
        this.request = request;
    }

    public UserLoginData getUserLoginData() throws PXException {
        if (pxConfiguration.isLoginCredentialsExtractionEnabled()) {
            final LoginCredentials credentials = extractCredentials();

            if (credentials != null && !credentials.isCredentialEmpty()) {
                return generateUserLoginData(credentials);
            }
        }
        return null;
    }

    private LoginCredentials extractCredentials() {
        final CredentialsExtractionDetails credentialsExtractionDetails = getCredentialsExtractionDetails();

        if (credentialsExtractionDetails != null) {
            return extractCredentials(credentialsExtractionDetails);
        }

        return null;
    }

    private CredentialsExtractionDetails getCredentialsExtractionDetails() {
        return pxConfiguration
                .getLoginCredentials()
                .getCredentialsExtractionDetails(request.getServletPath(), request.getMethod());
    }

    private LoginCredentials extractCredentials(CredentialsExtractionDetails credentialsExtractionDetails) {
        if (credentialsExtractionDetails.getCustomCallBack() != null) {
            final Function<HttpServletRequest, LoginCredentials> customCallBack = credentialsExtractionDetails.getCustomCallBack();

            return customCallBack.apply(request);
        } else {
            final CredentialsExtractorFactory credentialsExtractorFactory = new CredentialsExtractorFactory();
            final CredentialsExtractor credentialsExtractor = credentialsExtractorFactory.create(request, credentialsExtractionDetails);

            return credentialsExtractor.extractCredentials();
        }
    }

    private UserLoginData generateUserLoginData(LoginCredentials credentials) throws PXException {
        final CredentialsIntelligenceProtocolFactory factory = new CredentialsIntelligenceProtocolFactory();
        final CredentialsIntelligenceProtocol ciProtocol = factory.create(pxConfiguration.getCiVersion());

        return ciProtocol.generateUserLoginData(credentials);
    }
}
