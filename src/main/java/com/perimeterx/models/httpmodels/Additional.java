package com.perimeterx.models.httpmodels;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.risk.S2SCallReason;

/**
 * Created by shikloshi on 06/08/2016.
 */
public class Additional {

    @JsonProperty("px_cookie")
    public String PxCookie;
    @JsonProperty("http_method")
    public String HttpMethod;
    @JsonProperty("http_version")
    public String HttpVersion;
    @JsonProperty("s2s_call_reason")
    public S2SCallReason CallReason;
    @JsonProperty("module_version")
    public final String ModuleVersion = "Java SDK 1.0";

    public static Additional fromContext(PXContext ctx) {
        Additional additional = new Additional();
        additional.PxCookie = ctx.getPxCookie();
        additional.HttpMethod = ctx.getHttpMethod();
        additional.HttpVersion = ctx.getHttpVersion();
        additional.CallReason = ctx.getS2sCallReason();
        return additional;
    }
}
