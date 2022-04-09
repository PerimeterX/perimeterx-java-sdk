package com.perimeterx.api.additionals2s.credentialsIntelligence.loginrequest;

import com.perimeterx.models.configuration.credentialsIntelligenceconfig.ConfigCredentialsFieldPath;
import com.perimeterx.models.configuration.credentialsIntelligenceconfig.CredentialsExtractionDetails;

import javax.servlet.http.HttpServletRequest;

public class CredentialsExtractorFactory {
    public CredentialsExtractor create(HttpServletRequest request, CredentialsExtractionDetails credentialsExtractionDetails) {
        final ConfigCredentialsFieldPath credentialsFieldPath = credentialsExtractionDetails.getConfigCredentialsFieldPath();

        switch (credentialsExtractionDetails.getCredentialsLocationInRequest()) {
            case BODY:
                return new RequestBodyExtractor(request, credentialsFieldPath);
            case HEADER:
                return new RequestHeaderExtractor(request, credentialsFieldPath);
            case QUERY_PARAM:
                return new RequestQueryParamsExtractor(request, credentialsFieldPath);
            default:
                return null;
        }
    }
}
