package com.perimeterx.api.additionalContext.credentialsIntelligence.loginrequest;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.perimeterx.http.RequestWrapper;
import com.perimeterx.models.configuration.credentialsIntelligenceconfig.ConfigCredentialsFieldPath;
import com.perimeterx.models.configuration.credentialsIntelligenceconfig.LoginCredentials;
import com.perimeterx.utils.PXLogger;
import lombok.AllArgsConstructor;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

import static com.perimeterx.utils.StringUtils.extractCredentialsFromMultipart;
import static com.perimeterx.utils.StringUtils.splitQueryParams;

@AllArgsConstructor
public class RequestBodyExtractor implements CredentialsExtractor {
    private final static PXLogger logger = PXLogger.getLogger(RequestQueryParamsExtractor.class);

    private final static String CREDENTIALS_PATH_SEPARATOR = "\\.";
    private final static String CONTENT_TYPE = "content-type";
    private final static String X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
    private final static String MULTIPART_FORM_DATA = "multipart/form-data";
    private final static String APPLICATION_JSON = "application/json";

    private final ConfigCredentialsFieldPath credentialsFieldPath;

    @Override
    public LoginCredentials extractCredentials(HttpServletRequest request) {
        try {
            final String requestContentType = request.getHeader(CONTENT_TYPE);

            if (requestContentType.equals(X_WWW_FORM_URLENCODED)) {

                return extractFromFormURLEncoded(request);
            } else if (requestContentType.contains(MULTIPART_FORM_DATA)) {

                return extractCredentialsFromMultipart(((RequestWrapper) request).getBody(), credentialsFieldPath);
            } else if (requestContentType.equals(APPLICATION_JSON)){

                return extractRequestBodyFields(request);
            } else {
                logger.error("Failed to extract credentials from request body - unsupported content type :: " + requestContentType);
            }
        } catch (Exception e) {
            logger.error("Failed to extract credentials from request body. error :: ", e);
        }

        return null;
    }

    private LoginCredentials extractFromFormURLEncoded(HttpServletRequest request) throws IOException {
        final Map<String, String> queryParams = splitQueryParams(((RequestWrapper) request).getBody());

        return new LoginCredentials(
                queryParams.get(credentialsFieldPath.getUsernameFieldPath()),
                queryParams.get(credentialsFieldPath.getPasswordFieldPath())
        );
    }

    private LoginCredentials extractRequestBodyFields(HttpServletRequest request) throws IOException {
        final String username = extractFromJson(request, credentialsFieldPath.getUsernameFieldPath());
        final String password = extractFromJson(request, credentialsFieldPath.getPasswordFieldPath());

        return new LoginCredentials(username, password);
    }

    private String extractFromJson(HttpServletRequest request, String fieldPath) throws IOException {
        final String[] path = fieldPath.split(CREDENTIALS_PATH_SEPARATOR);
        final int lastIndexInPath = path.length - 1;
        JsonObject body = new Gson().fromJson(request.getReader(), JsonObject.class);

        for (int i = 0; i < path.length; i++) {

            if (i == lastIndexInPath) {
                return body.getAsJsonPrimitive(path[lastIndexInPath]).getAsString();
            }
            body = body.getAsJsonObject(path[i]);
        }

        return null;
    }
}
