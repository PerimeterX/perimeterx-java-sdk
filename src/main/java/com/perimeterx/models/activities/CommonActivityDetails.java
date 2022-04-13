package com.perimeterx.models.activities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.perimeterx.api.additionalContext.AdditionalContext;
import com.perimeterx.api.additionalContext.credentialsIntelligence.CIProtocol;
import com.perimeterx.api.additionalContext.credentialsIntelligence.SSOStep;
import com.perimeterx.models.PXContext;
import lombok.Getter;

import java.util.UUID;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonActivityDetails implements ActivityDetails {

    @JsonProperty("ci_version")
    public CIProtocol ciProtocol;

    @JsonProperty("sso_step")
    public SSOStep ssoStep;

    @JsonProperty("credentials_compromised")
    public boolean credentialsCompromised;

    @JsonProperty("request_id")
    public UUID requestId;

    public CommonActivityDetails(PXContext context) {
        final AdditionalContext additionalContext = context.getAdditionalContext();

        if(additionalContext != null && additionalContext.getLoginCredentials() != null) {
            this.ciProtocol = additionalContext.getLoginCredentials().getCiProtocol();
            this.ssoStep = additionalContext.getLoginCredentials().getSsoStep();
            this.credentialsCompromised = context.isBreachedAccount();
        }

        this.requestId = context.getRequestId();
    }
}
