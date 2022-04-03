package com.perimeterx.models.activities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.perimeterx.api.additionals2s.credentialsIntelligence.CIVersion;
import com.perimeterx.api.additionals2s.credentialsIntelligence.SSOStep;
import com.perimeterx.models.PXContext;

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

    public AdditionalS2SActivity(PXContext context) {
        this.clientUuid = context.getUuid();
        this.username = null;
        this.ciVersion = context.getLoginCredentials().getCiVersion();
        this.ssoStep = context.getLoginCredentials().getSsoStep();
        this.credentialsCompromised = context.isBreachedAccount();
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
