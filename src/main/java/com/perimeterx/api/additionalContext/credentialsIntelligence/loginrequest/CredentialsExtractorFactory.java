package com.perimeterx.api.additionalContext.credentialsIntelligence.loginrequest;

import com.perimeterx.models.configuration.credentialsIntelligenceconfig.ConfigCredentialsFieldPath;
import com.perimeterx.models.configuration.credentialsIntelligenceconfig.CredentialsExtractionDetails;

public class CredentialsExtractorFactory {
    public static CredentialsExtractor create(CredentialsExtractionDetails credentialsExtractionDetails) {
        final ConfigCredentialsFieldPath credentialsFieldPath = credentialsExtractionDetails.getConfigCredentialsFieldPath();

        switch (credentialsExtractionDetails.getCredentialsLocationInRequest()) {
            case BODY:
                return new RequestBodyExtractor(credentialsFieldPath);
            case HEADER:
                return new RequestHeaderExtractor(credentialsFieldPath);
            case QUERY_PARAM:
                return new RequestQueryParamsExtractor(credentialsFieldPath);
            default:
                return null;
        }
    }
}
