package com.perimeterx.api.additionalContext.credentialsIntelligence.loginrequest;

import com.perimeterx.api.PerimeterX;
import com.perimeterx.models.configuration.credentialsIntelligenceconfig.ConfigCredentialsFieldPath;
import com.perimeterx.models.configuration.credentialsIntelligenceconfig.LoginCredentials;
import com.perimeterx.utils.logger.IPXLogger;
import lombok.AllArgsConstructor;

import javax.servlet.http.HttpServletRequest;

@AllArgsConstructor
public class RequestQueryParamsExtractor implements CredentialsExtractor {

    private final ConfigCredentialsFieldPath credentialsFieldPath;
    private final IPXLogger logger;

    @Override
    public LoginCredentials extractCredentials(HttpServletRequest request) {
        try {
            final String username = request.getParameter(credentialsFieldPath.getUsernameFieldPath());
            final String password = request.getParameter(credentialsFieldPath.getPasswordFieldPath());

            return new LoginCredentials(username, password);
        } catch (Exception e) {
            this.logger.error("Failed to extract credentials from request query params. error :: ", e.getMessage());
            return null;
        }
    }
}
