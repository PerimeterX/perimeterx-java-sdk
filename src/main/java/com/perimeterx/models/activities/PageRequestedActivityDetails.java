package com.perimeterx.models.activities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.risk.CustomParameters;
import com.perimeterx.models.risk.PassReason;
import com.perimeterx.models.risk.S2SErrorReason;
import com.perimeterx.utils.Constants;

/**
 * Created by shikloshi on 07/11/2016.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PageRequestedActivityDetails implements ActivityDetails {

    @JsonProperty("http_method")
    private String httpMethod;
    @JsonProperty("http_version")
    private String httpVersion;
    @JsonProperty("px_cookie")
    private String riskCookie;
    @JsonProperty("pass_reason")
    private PassReason passReason;
    @JsonProperty("s2s_error_reason")
    private S2SErrorReason s2SErrorReason;
    @JsonProperty("s2s_error_message")
    private String s2sErrorMessage;
    @JsonProperty("s2s_error_http_status")
    private int s2sErrorHttpStatus;
    @JsonProperty("s2s_error_http_message")
    private String s2sErrorHttpMessage;
    @JsonProperty("risk_rtt")
    private long riskRtt;
    @JsonProperty("module_version")
    private String moduleVersion;
    @JsonProperty("client_uuid")
    private String clientUuid;
    @JsonProperty("cookie_origin")
    private String cookieOrigin;
    @JsonUnwrapped
    private CustomParameters customParameters;

    public PageRequestedActivityDetails(PXContext context) {
        this.httpMethod = context.getHttpMethod();
        this.httpVersion = context.getHttpVersion();
        this.riskCookie = context.getRiskCookie();
        this.passReason = context.getPassReason();
        this.s2SErrorReason = context.getS2sErrorReasonInfo().getReason();
        this.s2sErrorMessage = context.getS2sErrorReasonInfo().getMessage();
        this.s2sErrorHttpStatus = context.getS2sErrorReasonInfo().getHttpStatus();
        this.s2sErrorHttpMessage = context.getS2sErrorReasonInfo().getHttpMessage();
        this.riskRtt = context.getRiskRtt();
        this.moduleVersion = Constants.SDK_VERSION;
        this.clientUuid = context.getUuid();
        this.cookieOrigin = context.getCookieOrigin();
        this.customParameters = context.getCustomParameters();
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public String getRiskCookie() {
        return riskCookie;
    }

    public PassReason getPassReason() {
        return passReason;
    }

    public long getRiskRtt() {
        return riskRtt;
    }

    public String getModuleVersion() {
        return moduleVersion;
    }

    public String getClientUuid() {
        return clientUuid;
    }

    public CustomParameters getCustomParameters() {
        return customParameters;
    }
}
