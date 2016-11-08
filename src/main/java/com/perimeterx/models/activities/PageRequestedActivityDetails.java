package com.perimeterx.models.activities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    public PageRequestedActivityDetails(PXContext context) {
        this.httpMethod = context.getHttpMethod();
        this.httpVersion = context.getHttpVersion();
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getHttpVersion() {
        return httpVersion;
    }
}
