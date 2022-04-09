package com.perimeterx.models.httpmodels;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.perimeterx.api.additionals2s.credentialsIntelligence.CIVersion;
import com.perimeterx.api.additionals2s.credentialsIntelligence.SSOStep;
import com.perimeterx.api.additionals2s.credentialsIntelligence.UserLoginData;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.risk.CustomParameters;
import com.perimeterx.utils.Constants;

import java.util.UUID;

/**
 * Created by shikloshi on 06/08/2016.
 */
public class Additional {

    @JsonProperty("px_cookie")
    public String pxCookie;

    @JsonProperty("http_method")
    public String httpMethod;

    @JsonProperty("http_version")
    public String httpVersion;

    @JsonProperty("s2s_call_reason")
    public String callReason;

    @JsonProperty("px_cookie_raw")
    public String pxCookieRaw;

    @JsonProperty("cookie_origin")
    public String pxCookieOrigin;

    @JsonProperty("module_version")
    public final String moduleVersion = Constants.SDK_VERSION;

    @JsonProperty("original_uuid")
    public String originalUuid;

    @JsonProperty("original_token_error")
    public String originalTokenError;

    @JsonProperty("original_token")
    public String originalToken;

    @JsonProperty("decoded_original_token")
    public String decodedOriginalToken;

    @JsonProperty("risk_mode")
    public String riskMode;

    @JsonProperty("px_cookie_hmac")
    public String pxCookieHmac;

    @JsonUnwrapped
    public CustomParameters customParameters;

    @JsonProperty("request_cookie_names")
    public String[] requestCookieNames;

    @JsonProperty("enforcer_vid_source")
    public String vidSource;

    @JsonProperty("user")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String username;

    @JsonProperty("pass")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String password;

    @JsonProperty("ci_version")
    public CIVersion ciVersion;

    @JsonProperty("sso_step")
    public SSOStep ssoStep;

    @JsonProperty("request_id")
    public UUID requestId;

    public static Additional fromContext(PXContext ctx) {
        Additional additional = new Additional();
        additional.pxCookie = ctx.getRiskCookie();
        additional.httpMethod = ctx.getHttpMethod();
        additional.httpVersion = ctx.getHttpVersion();
        additional.callReason = ctx.getS2sCallReason();
        additional.pxCookieRaw = ctx.getPxCookieRaw();
        additional.pxCookieOrigin = ctx.getCookieOrigin();
        additional.customParameters = ctx.getCustomParameters();
        additional.originalUuid = ctx.getOriginalUuid();
        additional.originalTokenError = ctx.getOriginalTokenError();
        additional.originalToken = ctx.getPxOriginalTokenCookie();
        additional.decodedOriginalToken = ctx.getDecodedOriginalToken();
        additional.riskMode = ctx.getRiskMode();
        additional.pxCookieHmac = ctx.getCookieHmac();
        additional.requestCookieNames = ctx.getRequestCookieNames();
        additional.vidSource = ctx.getVidSource().getValue();
        additional.requestId = ctx.getRequestId();

        setLoginCredentials(ctx, additional);

        return additional;
    }

    private static void setLoginCredentials(PXContext ctx, Additional additional) {
        if(ctx.isContainCredentialsIntelligence()) {
            final UserLoginData loginCredentials = ctx.getAdditionalS2SContext().getLoginCredentials();
            additional.username = loginCredentials.getUsername();
            additional.password = loginCredentials.getEncodedPassword();
            additional.ciVersion = loginCredentials.getCiVersion();
            additional.ssoStep = loginCredentials.getSsoStep();
        }
    }
}
