package com.perimeterx.api.additionals2s.credentialsIntelligence.loginrequest;

import com.perimeterx.models.configuration.credentialsIntelligenceconfig.ConfigCredentialsFieldPath;
import com.perimeterx.models.configuration.credentialsIntelligenceconfig.LoginCredentials;
import com.perimeterx.utils.PXLogger;
import lombok.AllArgsConstructor;

import javax.servlet.http.HttpServletRequest;

@AllArgsConstructor
public class RequestQueryParamsExtractor implements CredentialsExtractor {
    private final static PXLogger logger = PXLogger.getLogger(RequestQueryParamsExtractor.class);

    private final ConfigCredentialsFieldPath credentialsFieldPath;

    @Override
    public LoginCredentials extractCredentials(HttpServletRequest request) {
        try {
            final String username = request.getParameter(credentialsFieldPath.getUsernameFieldPath());
            final String password = request.getParameter(credentialsFieldPath.getPasswordFieldPath());

            return new LoginCredentials(username, password);
        } catch (Exception e) {
            logger.error("Failed to extract credentials from request query params. error :: ", e);
            return null;
        }
    }
}
