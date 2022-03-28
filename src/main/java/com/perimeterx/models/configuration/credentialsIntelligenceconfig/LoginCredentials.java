package com.perimeterx.models.configuration.credentialsIntelligenceconfig;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LoginCredentials {
    private final String username;
    private final String password;
}
