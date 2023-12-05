package com.perimeterx.models.activities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.perimeterx.api.additionalContext.LoginData;
import com.perimeterx.api.additionalContext.credentialsIntelligence.CIProtocol;
import com.perimeterx.api.additionalContext.credentialsIntelligence.SSOStep;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.httpmodels.Additional;
import lombok.Getter;

import java.util.UUID;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonActivityDetails implements ActivityDetails {

    @JsonProperty("enforcer_start_time")
    public long enforcerStartTime;
    @JsonProperty("risk_start_time")
    public long riskStartTime;
    @JsonProperty("px_decoded_original_token")
    public String decodedOriginalToken;
    @JsonProperty("enforcer_vid_source")
    public String vidSource;
    @JsonProperty("original_token_error")
    public String originalTokenError;
    @JsonProperty("original_token")
    public String originalToken;
    @JsonProperty("original_uuid")
    public String originalUuid;
    @JsonProperty("px_orig_cookie")
    public String pxCookieOrigin;
    @JsonProperty("risk_mode")
    public String riskMode;
    @JsonProperty("s2s_call_reason")
    public String callReason;
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

        this.additionalRiskInfo = context.getAdditionalRiskInfo();
        this.requestId = context.getRequestId();

        Additional additional = com.perimeterx.models.httpmodels.Additional.fromContext(context);
        this.decodedOriginalToken = additional.decodedOriginalToken;
        this.vidSource = additional.vidSource;
        this.originalTokenError = additional.originalTokenError;
        this.originalToken = additional.originalToken;
        this.originalUuid = additional.originalUuid;
        this.pxCookieOrigin = additional.pxCookieOrigin;
        this.riskMode = additional.riskMode;
        this.callReason = additional.callReason;
        this.riskStartTime = additional.riskStartTime;
        this.enforcerStartTime = additional.enforcerStartTime;

    }
}
