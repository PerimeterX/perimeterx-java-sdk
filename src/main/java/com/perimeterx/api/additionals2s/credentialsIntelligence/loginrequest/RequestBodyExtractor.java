package com.perimeterx.api.additionals2s.credentialsIntelligence.loginrequest;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.perimeterx.http.RequestWrapper;
import com.perimeterx.models.configuration.credentialsIntelligenceconfig.ConfigCredentialsFieldPath;
import com.perimeterx.models.configuration.credentialsIntelligenceconfig.LoginCredentials;
import com.perimeterx.utils.PXLogger;
import lombok.AllArgsConstructor;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Map;

import static com.perimeterx.utils.StringUtils.splitQueryParams;
import static org.apache.commons.lang3.StringUtils.strip;

@AllArgsConstructor
public class RequestBodyExtractor implements CredentialsExtractor {
    private final PXLogger logger = PXLogger.getLogger(RequestQueryParamsExtractor.class);

    private final static String MULTIPART_CONTENT_SEPARATOR = "Content-Disposition: form-data; name=";
    private final static String MULTIPART_DASH_SEPARATOR = "--";
    private final static int DOUBLE_QUOTES_CHAR_LENGTH = 1;
    private final static String CREDENTIALS_PATH_SEPARATOR = "\\.";
    private final static String CONTENT_TYPE = "content-type";
    private final static String X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
    private final static String MULTIPART_FORM_DATA = "multipart/form-data";

    private final HttpServletRequest request;
    private final ConfigCredentialsFieldPath credentialsFieldPath;

    @Override
    public LoginCredentials extractCredentials() {
        try {
            if (request.getHeader(CONTENT_TYPE).equals(X_WWW_FORM_URLENCODED)) {

                return extractFromQueryParamsStructure();
            } else if (request.getHeader(CONTENT_TYPE).contains(MULTIPART_FORM_DATA)) {

                return extractFromMultipartHeaderTemplate(((RequestWrapper) request).getBody(), credentialsFieldPath);
            } else {

                return extractRequestBodyFields();
            }
        } catch (Exception e) {
            logger.error("Failed to extract credentials from request body. error :: ", e);
            return null;
        }
    }

    private LoginCredentials extractFromQueryParamsStructure() throws UnsupportedEncodingException {
        final Map<String, String> queryParams = splitQueryParams(((RequestWrapper) request).getBody());

        return new LoginCredentials(
                queryParams.get(credentialsFieldPath.getUsernameFieldPath()),
                queryParams.get(credentialsFieldPath.getPasswordFieldPath())
        );
    }

    private LoginCredentials extractFromMultipartHeaderTemplate(String body, ConfigCredentialsFieldPath credentialsFieldPath) {
        final String bodyWithoutEdgeDashes = strip(body, MULTIPART_DASH_SEPARATOR);
        final String[] arr = bodyWithoutEdgeDashes.split(MULTIPART_CONTENT_SEPARATOR);

        final String usernameKey = credentialsFieldPath.getUsernameFieldPath();
        final String passwordKey = credentialsFieldPath.getPasswordFieldPath();
        final int usernameLength = usernameKey.length() + DOUBLE_QUOTES_CHAR_LENGTH;
        final int passwordLength = passwordKey.length() + DOUBLE_QUOTES_CHAR_LENGTH;

        final LoginCredentials credentials = new LoginCredentials();

        Arrays.stream(arr).forEach(header -> {
            if (header.substring(1, usernameLength).equals(usernameKey)) {
                credentials.setUsername(extractSubstring(header, usernameLength + DOUBLE_QUOTES_CHAR_LENGTH));
            } else if (header.substring(1, passwordLength).equals(passwordKey)) {
                credentials.setPassword(extractSubstring(header, passwordLength + DOUBLE_QUOTES_CHAR_LENGTH));
            }
        });

        return credentials;
    }

    private String extractSubstring(String header, int keyLength) {
        int keyValueIndex = header.lastIndexOf(MULTIPART_DASH_SEPARATOR);
        return header.substring(keyLength, keyValueIndex);
    }

    private LoginCredentials extractRequestBodyFields() throws IOException {
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
