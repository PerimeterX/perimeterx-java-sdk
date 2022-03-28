package com.perimeterx.models.configuration.credentialsIntelligenceconfig;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.servlet.http.HttpServletRequest;
import java.util.function.Function;

@NoArgsConstructor
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginCredentialsConfig {

    @JsonProperty("method")
    private LoginRequestMethod method;

    @JsonProperty("path")
    private String path;

    @JsonProperty("sent_through")
    private CredentialsLocationInRequest credentialsLocationInRequest;

    @JsonProperty("pass_field")
    private String password;

    @JsonProperty("user_field")
    private String userName;

    @JsonProperty("path_type")
    private PathType pathType;

    @JsonProperty("callback")
    private Function<HttpServletRequest,LoginCredentials> customCallback;
}