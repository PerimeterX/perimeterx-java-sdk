package com.perimeterx.api.additionalContext.credentialsIntelligence.loginrequest;

import com.perimeterx.models.configuration.credentialsIntelligenceconfig.ConfigCredentialsFieldPath;
import com.perimeterx.models.configuration.credentialsIntelligenceconfig.CredentialsExtractionDetails;
import com.perimeterx.utils.logger.IPXLogger;

public class CredentialsExtractorFactory {
    public static CredentialsExtractor create(CredentialsExtractionDetails credentialsExtractionDetails, IPXLogger logger) {
        final ConfigCredentialsFieldPath credentialsFieldPath = credentialsExtractionDetails.getConfigCredentialsFieldPath();

        switch (credentialsExtractionDetails.getCredentialsLocationInRequest()) {
            case BODY:
                return new RequestBodyExtractor(credentialsFieldPath,logger);
            case HEADER:
                return new RequestHeaderExtractor(credentialsFieldPath,logger);
            case QUERY_PARAM:
                return new RequestQueryParamsExtractor(credentialsFieldPath,logger);
            default:
                logger.error("Can't create credentials extractor - request credential location is invalid");
                return null;
        }
    }
}
