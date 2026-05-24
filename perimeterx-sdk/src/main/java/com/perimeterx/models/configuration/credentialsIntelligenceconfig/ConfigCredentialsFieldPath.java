package com.perimeterx.models.configuration.credentialsIntelligenceconfig;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ConfigCredentialsFieldPath {
    private final String usernameFieldPath;
    private final String passwordFieldPath;
}
