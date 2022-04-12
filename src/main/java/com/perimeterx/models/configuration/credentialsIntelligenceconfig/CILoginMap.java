package com.perimeterx.models.configuration.credentialsIntelligenceconfig;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.perimeterx.utils.PXLogger;
import lombok.Getter;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** This Map represents the json configuration of the login routes **/

@Getter
public class CILoginMap {
    private static final PXLogger logger = PXLogger.getLogger(CILoginMap.class);
    private final static String KEY_DELIMITER = ":";

    private final Map<String, CredentialsExtractionDetails> pathAndMethodToLoginExtractionDetails;
    private final Map<String, CredentialsExtractionDetails> regexPathAndMethodToLoginExtractionDetails;

    public CILoginMap(String jsonLoginCredentials) {
        this.pathAndMethodToLoginExtractionDetails = new HashMap<>();
        this.regexPathAndMethodToLoginExtractionDetails = new HashMap<>();

        setMapValues(generateLoginCredentialsConfig(jsonLoginCredentials));
    }

    private void setMapValues(Collection<LoginCredentialsConfig> loginCredentials) {
        loginCredentials.forEach(lc -> {
            final String key = generateMapKey(lc.getPath(), lc.getMethod().toString());
            final ConfigCredentialsFieldPath credentialsFieldNames = new ConfigCredentialsFieldPath(lc.getUsernameField(), lc.getPasswordField());
            final CredentialsExtractionDetails credentialsExtractionDetails = new CredentialsExtractionDetails(
                    lc.getCredentialsLocationInRequest(), credentialsFieldNames);

            if(isRegex(lc)) {
                regexPathAndMethodToLoginExtractionDetails.put(key, credentialsExtractionDetails);
            } else {
                pathAndMethodToLoginExtractionDetails.put(key, credentialsExtractionDetails);
            }
        });
    }

    private Collection<LoginCredentialsConfig> generateLoginCredentialsConfig(String jsonLoginCredentials) {
        final ObjectMapper objectMapper = new ObjectMapper();

        try {
             return objectMapper.readValue(jsonLoginCredentials, new TypeReference<Collection<LoginCredentialsConfig>>() {
            });
        } catch (JsonProcessingException jpe) {
            logger.error("Failed to extract px_login_credentials_extraction configuration to login credentials. Error :: " + jpe);

            return Collections.EMPTY_LIST;
        }
    }

    private boolean isRegex(LoginCredentialsConfig lc) {
        return lc.getPathType() != null && lc.getPathType().equals(PathType.REGEX);
    }

    public CredentialsExtractionDetails getCredentialsExtractionDetails(String path, String method) {
        final String key = generateMapKey(path, method);

        if(pathAndMethodToLoginExtractionDetails.containsKey(key)) {
            return pathAndMethodToLoginExtractionDetails.get(key);
        } else {
            for (String regexKey : regexPathAndMethodToLoginExtractionDetails.keySet()) {
                final boolean regexLoginRoute = isRegexLoginRoute(path, method, regexKey);

                if(regexLoginRoute) {
                    return regexPathAndMethodToLoginExtractionDetails.get(regexKey);
                }
            }
        }

        return null;
    }

    private boolean isRegexLoginRoute(String path, String method, String regexKey) {
        final String[] pathAndMethod = regexKey.split(KEY_DELIMITER);
        final Pattern regexPath = Pattern.compile(pathAndMethod[0], Pattern.CASE_INSENSITIVE);
        final Matcher matcher = regexPath.matcher(path);

        return pathAndMethod[1].equals(method) && matcher.find();
    }

    private String generateMapKey(String path, String method) {
        return path + KEY_DELIMITER + method;
    }
}
