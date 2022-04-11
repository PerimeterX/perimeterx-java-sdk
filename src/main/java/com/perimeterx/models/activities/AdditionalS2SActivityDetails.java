package com.perimeterx.models.activities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.perimeterx.api.additionals2s.credentialsIntelligence.AdditionalContext;
import com.perimeterx.api.additionals2s.credentialsIntelligence.CIProtocol;
import com.perimeterx.api.additionals2s.credentialsIntelligence.SSOStep;
import com.perimeterx.models.PXContext;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdditionalS2SActivityDetails implements ActivityDetails {

    @JsonProperty("client_uuid")
    private String clientUuid;

    @JsonProperty("raw_username")
    public String username;

    @JsonProperty("ci_version")
    public CIProtocol ciProtocol;

    @JsonProperty("sso_step")
    public SSOStep ssoStep;

    @JsonProperty("credentials_compromised")
    public boolean credentialsCompromised;

    @JsonProperty("login_successful")
    public Boolean loginSuccessful;

    @JsonProperty("http_status_code")
    public Integer httpStatusCode;

    @JsonProperty("request_id")
    public UUID requestId;


    public AdditionalS2SActivityDetails(PXContext context) {
        final AdditionalContext additionalContext = context.getAdditionalContext();
        this.clientUuid = context.getUuid();
        this.username = null;
        this.ciProtocol = additionalContext.getLoginCredentials().getCiProtocol();
        this.ssoStep = additionalContext.getLoginCredentials().getSsoStep();
        this.credentialsCompromised = context.isBreachedAccount();
        this.loginSuccessful = additionalContext.getLoginSuccessful();
        this.httpStatusCode = additionalContext.getResponseStatusCode();
        this.requestId = context.getRequestId();

    }

    public void setUsername(String username) {
        this.username = username;
    }
}
