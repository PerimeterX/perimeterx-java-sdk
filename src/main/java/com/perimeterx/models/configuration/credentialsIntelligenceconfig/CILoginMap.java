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

@Getter
public class CILoginMap {
    private static final PXLogger logger = PXLogger.getLogger(CILoginMap.class);
    private final static String DELIMITER = ":";

    private final Map<String, CredentialsExtractionDetails> pathAndMethodToLoginExtractionDetails;
    private final Map<String, CredentialsExtractionDetails> regexPathAndMethodToLoginExtractionDetails;

    public CILoginMap(String jsonLoginCredentials) {
        this.pathAndMethodToLoginExtractionDetails = new HashMap<>();
        this.regexPathAndMethodToLoginExtractionDetails = new HashMap<>();

        setMapValues(generateLoginCredentialsConfig(jsonLoginCredentials));
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

    private void setMapValues(Collection<LoginCredentialsConfig> loginCredentials) {
        loginCredentials.forEach(lc -> {
            final String key = generateMapKey(lc.getPath(), lc.getMethod().toString());
            final ConfigCredentialsFieldNames credsFieldNames = new ConfigCredentialsFieldNames(lc.getUserName(), lc.getPassword());
            final CredentialsExtractionDetails credentialsExtractionDetails = new CredentialsExtractionDetails(
                    lc.getCredentialsLocationInRequest(), lc.getCustomCallback(), credsFieldNames);

            if(isRegex(lc)) {
                regexPathAndMethodToLoginExtractionDetails.put(key, credentialsExtractionDetails);
            } else {
                pathAndMethodToLoginExtractionDetails.put(key, credentialsExtractionDetails);
            }
        });
    }

    private String generateMapKey(String path, String method) {
        return path + DELIMITER + method;
    }

    private boolean isRegex(LoginCredentialsConfig lc) {
        return lc.getPathType() != null && lc.getPathType().equals(PathType.REGEX);
    }
}
