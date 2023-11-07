package com.perimeterx.models.configuration.credentialsIntelligenceconfig;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static org.apache.commons.lang3.StringUtils.isBlank;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoginCredentials {
    private String username;
    private String password;

    public boolean isCredentialsEmpty() {
        return isBlank(this.getUsername())
                && isBlank(this.getPassword());
    }
}
