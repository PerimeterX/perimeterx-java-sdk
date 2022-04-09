package com.perimeterx.models.activities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.perimeterx.api.additionals2s.credentialsIntelligence.AdditionalS2SContext;
import com.perimeterx.api.additionals2s.credentialsIntelligence.CIVersion;
import com.perimeterx.api.additionals2s.credentialsIntelligence.SSOStep;
import com.perimeterx.models.PXContext;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdditionalS2SActivity implements ActivityDetails {

    @JsonProperty("client_uuid")
    private String clientUuid;

    @JsonProperty("raw_username")
    public String username;

    @JsonProperty("ci_version")
    public CIVersion ciVersion;

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


    public AdditionalS2SActivity(PXContext context) {
        final AdditionalS2SContext additionalS2SContext = context.getAdditionalS2SContext();
        this.clientUuid = context.getUuid();
        this.username = null;
        this.ciVersion = additionalS2SContext.getLoginCredentials().getCiVersion();
        this.ssoStep = additionalS2SContext.getLoginCredentials().getSsoStep();
        this.credentialsCompromised = context.isBreachedAccount();
        this.loginSuccessful = additionalS2SContext.getLoginSuccessful();
        this.httpStatusCode = additionalS2SContext.getResponseStatusCode();
        this.requestId = context.getRequestId();

    }

    public void setUsername(String username) {
        this.username = username;
    }
}
