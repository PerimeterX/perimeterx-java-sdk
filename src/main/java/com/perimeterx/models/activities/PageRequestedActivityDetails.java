package com.perimeterx.models.activities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.risk.PassReason;
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
    @JsonProperty("risk_rtt")
    private long riskRtt;
    @JsonProperty("module_version")
    private String moduleVersion;
    @JsonProperty("client_uuid")
    private String clientUuid;
    @JsonProperty("cookie_origin")
    private String cookieOrigin;

    public PageRequestedActivityDetails(PXContext context) {
        this.httpMethod = context.getHttpMethod();
        this.httpVersion = context.getHttpVersion();
        this.riskCookie = context.getRiskCookie();
        this.passReason = context.getPassReason();
        this.riskRtt = context.getRiskRtt();
        this.moduleVersion = Constants.SDK_VERSION;
        this.clientUuid = context.getUuid();
        this.cookieOrigin = context.getCookieOrigin();
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
}
