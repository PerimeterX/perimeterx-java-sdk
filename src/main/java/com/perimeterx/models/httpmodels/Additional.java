package com.perimeterx.models.httpmodels;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.risk.S2SCallReason;

/**
 * Created by shikloshi on 06/08/2016.
 */
public class Additional {

    @JsonProperty("px_cookie")
    private String pxCookie;
    @JsonProperty("http_method")
    private String httpMethod;
    @JsonProperty("http_version")
    private String httpVersion;
    @JsonProperty("s2s_call_reason")
    private S2SCallReason callReason;
    @JsonProperty("module_version")
    private String moduleVersion = "Java SDK 1.0";

    public Additional(PXContext ctx) {
        this.pxCookie = ctx.getPxCookie();
        this.httpMethod = ctx.getHttpMethod();
        this.httpVersion = ctx.getHttpVersion();
        this.callReason = ctx.getS2sCallReason();
    }

    public Additional() {
    }

    public String getPxCookie() {
        return pxCookie;
    }

    public void setPxCookie(String pxCookie) {
        this.pxCookie = pxCookie;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public void setHttpVersion(String httpVersion) {
        this.httpVersion = httpVersion;
    }

    public S2SCallReason getCallReason() {
        return callReason;
    }

    public void setCallReason(S2SCallReason callReason) {
        this.callReason = callReason;
    }

    public String getModuleVersion() {
        return moduleVersion;
    }

    public void setModuleVersion(String moduleVersion) {
        this.moduleVersion = moduleVersion;
    }
}
