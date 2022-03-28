package com.perimeterx.models.configuration.credentialsIntelligenceconfig;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.servlet.http.HttpServletRequest;
import java.util.function.Function;

@AllArgsConstructor
@Getter
public class CredentialsExtractionDetails {
    private final CredentialsLocationInRequest credentialsLocationInRequest;
    private final Function<HttpServletRequest, LoginCredentials> customCallBack;
    private final ConfigCredentialsFieldNames configCredentialsFieldNames;
}
