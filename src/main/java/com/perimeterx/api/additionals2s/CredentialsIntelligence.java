package com.perimeterx.api.additionals2s;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.perimeterx.api.additionals2s.credentialsIntelligence.CredentialsIntelligenceProtocol;
import com.perimeterx.api.additionals2s.credentialsIntelligence.CredentialsIntelligenceProtocolFactory;
import com.perimeterx.api.additionals2s.credentialsIntelligence.UserLoginData;
import com.perimeterx.http.RequestWrapper;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.configuration.credentialsIntelligenceconfig.ConfigCredentialsFieldPath;
import com.perimeterx.models.configuration.credentialsIntelligenceconfig.CredentialsExtractionDetails;
import com.perimeterx.models.configuration.credentialsIntelligenceconfig.LoginCredentials;
import com.perimeterx.models.exceptions.PXException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.function.Function;

import static com.perimeterx.utils.StringUtils.splitQueryParams;

public class CredentialsIntelligence {
    private static final String CREDENTIALS_PATH_SEPARATOR = "\\.";

    private final PXConfiguration pxConfiguration;
    private HttpServletRequest request;

    public CredentialsIntelligence(PXConfiguration pxConfiguration, HttpServletRequest request) {
        this.pxConfiguration = pxConfiguration;
        this.request = request;
    }

    public UserLoginData getUserLoginData() throws PXException, IOException {
        if (pxConfiguration.isLoginCredentialsExtractionEnabled()) {
            final LoginCredentials rawCredentials = extractCredentials();

            if (rawCredentials != null) {
                return generateUserLoginData(rawCredentials);
            }
        }
        return null;
    }

    private UserLoginData generateUserLoginData(LoginCredentials rawCredentials) throws PXException {
        final CredentialsIntelligenceProtocol ciProtocol = CredentialsIntelligenceProtocolFactory.create(pxConfiguration.getCiVersion());

        return ciProtocol.generateUserLoginData(rawCredentials);
    }

    private LoginCredentials extractCredentials() throws PXException, IOException {
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

    private LoginCredentials extractCredentials(CredentialsExtractionDetails credentialsExtractionDetails) throws PXException, IOException {
        if (credentialsExtractionDetails.getCustomCallBack() != null) {
            final Function<HttpServletRequest, LoginCredentials> customCallBack = credentialsExtractionDetails.getCustomCallBack();
            return customCallBack.apply(request);
        } else {
            return extractByLocation(credentialsExtractionDetails);
        }
    }

    private LoginCredentials extractByLocation(CredentialsExtractionDetails credentialsExtractionDetails) throws PXException, UnsupportedEncodingException {
        final ConfigCredentialsFieldPath credentialsFieldPath = credentialsExtractionDetails.getConfigCredentialsFieldPath();

        switch (credentialsExtractionDetails.getCredentialsLocationInRequest()) {
            case BODY:
                return extractRequestBodyFields(credentialsFieldPath);
            case HEADER:
                return extractBodyParams(credentialsFieldPath);
            case QUERY_PARAM:
                return extractRequestParams(credentialsFieldPath);
            default:
                return null;
        }
    }

    private LoginCredentials extractRequestBodyFields(ConfigCredentialsFieldPath configCredentialsFieldPath) throws PXException {
        final String username = extractFromJson(configCredentialsFieldPath.getUsernameFieldPath());
        final String password = extractFromJson(configCredentialsFieldPath.getPasswordFieldPath());

        return new LoginCredentials(username, password);
    }

    private String extractFromJson(String fieldPath) throws PXException {
        final String[] path = fieldPath.split(CREDENTIALS_PATH_SEPARATOR);

        try {
            return getNestedField(path);
        } catch (IOException ioe) {
            throw new PXException("Failed to extract field credentials by field path :: " + fieldPath + ". Error :: " + ioe);
        }
    }

    private String getNestedField(String[] path) throws IOException {
        JsonObject body = new Gson().fromJson(request.getReader(), JsonObject.class);

        for (int i = 0; i < path.length; i++) {

            if (i == path.length - 1) {
                JsonPrimitive nestedField = body.getAsJsonPrimitive(path[path.length - 1]);
                return nestedField != null ? String.valueOf(nestedField) : null;
            }
            body = body.getAsJsonObject(path[i]);
        }

        return null;
    }

    private LoginCredentials extractBodyParams(ConfigCredentialsFieldPath configCredentialsFieldPath) throws UnsupportedEncodingException {
        final Map<String, String> queryParams = splitQueryParams(((RequestWrapper) request).getBody());

        return new LoginCredentials(queryParams.get(configCredentialsFieldPath.getUsernameFieldPath()),
                queryParams.get(configCredentialsFieldPath.getPasswordFieldPath()));
    }

    private LoginCredentials extractRequestParams(ConfigCredentialsFieldPath configCredentialsFieldPath) {
        final String username = request.getParameter(configCredentialsFieldPath.getUsernameFieldPath());
        final String password = request.getParameter(configCredentialsFieldPath.getPasswordFieldPath());

        return new LoginCredentials(username, password);
    }

}
