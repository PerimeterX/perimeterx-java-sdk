package com.perimeterx.models.configuration.credentialsIntelligenceconfig;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CredentialsExtractionDetails {
    private final CredentialsLocationInRequest credentialsLocationInRequest;
    private final ConfigCredentialsFieldPath configCredentialsFieldPath;
}
