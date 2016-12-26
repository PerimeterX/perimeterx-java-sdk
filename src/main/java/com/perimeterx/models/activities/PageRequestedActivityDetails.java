package com.perimeterx.models.activities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.perimeterx.internals.cookie.RiskCookie;
import com.perimeterx.models.PXContext;

/**
 * Created by shikloshi on 07/11/2016.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PageRequestedActivityDetails implements ActivityDetails{

    @JsonProperty("http_method")
    private String httpMethod;
    @JsonProperty("http_version")
    private String httpVersion;
    @JsonPropertyOrder("px_cookie")
    private RiskCookie riskCookie;


    public PageRequestedActivityDetails(PXContext context) {
        this.httpMethod = context.getHttpMethod();
        this.httpVersion = context.getHttpVersion();
        this.riskCookie = context.getRiskCookie();
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public RiskCookie getRiskCookie() {
        return riskCookie;
    }
}
