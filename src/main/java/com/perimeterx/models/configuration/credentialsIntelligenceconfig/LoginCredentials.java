package com.perimeterx.models.configuration.credentialsIntelligenceconfig;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoginCredentials {
    private String username;
    private String password;

    public boolean isCredentialEmpty() {
        return this.getUsername() == null && this.getPassword() == null;
    }
}
