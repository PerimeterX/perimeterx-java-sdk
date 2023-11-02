package com.perimeterx.models.activities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.perimeterx.api.additionalContext.LoginData;
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
    public Boolean credentialsCompromised;

    @JsonProperty("request_id")
    public UUID requestId;

    @JsonProperty("additional_risk_info")
    public String additionalRiskInfo;

    @JsonProperty("user")
    public String username;

    @JsonProperty("pass")
    public String password;

    public CommonActivityDetails(PXContext context) {
        final LoginData loginData = context.getLoginData();

        if(loginData != null && loginData.getLoginCredentials() != null) {
            this.ciProtocol = loginData.getLoginCredentials().getCiProtocol();
            this.ssoStep = loginData.getLoginCredentials().getSsoStep();
            this.credentialsCompromised = context.isBreachedAccount();
            this.username = loginData.getLoginCredentials().getUsername();
            this.password = loginData.getLoginCredentials().getEncodedPassword();
        }

        this.additionalRiskInfo = context.getAdditionalRiskInfo() != null ? context.getAdditionalRiskInfo() : null;
        this.requestId = context.getRequestId();
    }
}
