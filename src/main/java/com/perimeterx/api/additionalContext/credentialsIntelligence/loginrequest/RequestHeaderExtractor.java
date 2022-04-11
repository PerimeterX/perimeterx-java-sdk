package com.perimeterx.api.additionalContext.credentialsIntelligence.loginrequest;

import com.perimeterx.models.configuration.credentialsIntelligenceconfig.ConfigCredentialsFieldPath;
import com.perimeterx.models.configuration.credentialsIntelligenceconfig.LoginCredentials;
import com.perimeterx.utils.PXLogger;
import lombok.AllArgsConstructor;

import javax.servlet.http.HttpServletRequest;

@AllArgsConstructor
public class RequestHeaderExtractor implements CredentialsExtractor {
    private static final PXLogger logger = PXLogger.getLogger(RequestHeaderExtractor.class);

    private final ConfigCredentialsFieldPath credentialsFieldPath;

    @Override
    public LoginCredentials extractCredentials(HttpServletRequest request) {
        try {
            final String username = request.getHeader(credentialsFieldPath.getUsernameFieldPath());
            final String password = request.getHeader(credentialsFieldPath.getPasswordFieldPath());

            return new LoginCredentials(username, password);
        } catch (Exception e) {
            logger.error("Failed to extract credentials from request headers. error :: ", e);

            return null;
        }
    }
}