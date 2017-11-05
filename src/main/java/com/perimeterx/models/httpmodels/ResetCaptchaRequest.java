package com.perimeterx.models.httpmodels;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.risk.Request;

/**
 * ResetCaptchaRequest model
 * <p>
 * Created by shikloshi on 07/07/2016.
 */
public class ResetCaptchaRequest {

    @JsonProperty("request")
    public Request request;
    @JsonProperty("pxCaptcha")
    public String pxCaptcha;
    @JsonProperty("hostname")
    public String hostname;

    public static ResetCaptchaRequest fromContext(PXContext context, PXConfiguration pxConfiguration) {
        ResetCaptchaRequest resetCaptchaRequest = new ResetCaptchaRequest();
        resetCaptchaRequest.request = RequestCaptcha.fromContext(context, pxConfiguration);
        resetCaptchaRequest.hostname = context.getHostname();
        resetCaptchaRequest.pxCaptcha = context.getPxCaptcha();
        return resetCaptchaRequest;
    }

}
