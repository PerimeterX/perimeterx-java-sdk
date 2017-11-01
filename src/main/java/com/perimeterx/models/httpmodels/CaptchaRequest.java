package com.perimeterx.models.httpmodels;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.configuration.CaptchaProvider;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.risk.Request;

/**
 * CaptchaRequest model
 * <p>
 * Created by shikloshi on 07/07/2016.
 */
public class CaptchaRequest {

    @JsonProperty("request")
    public Request Request;
    @JsonProperty("vid")
    public String Vid;
    @JsonProperty("uuid")
    public String Uuid;
    @JsonProperty("pxCaptcha")
    public String PxCaptcha;
    @JsonProperty("hostname")
    public String Hostname;
    @JsonProperty("captchaType")
    public CaptchaProvider catpchaType;


    public static CaptchaRequest fromContext(PXContext context, PXConfiguration pxConfiguration) {
        CaptchaRequest captchaRequest = new CaptchaRequest();
        captchaRequest.Request = com.perimeterx.models.risk.Request.fromContext(context);
        captchaRequest.PxCaptcha = context.getPxCaptcha();
        captchaRequest.Uuid = context.getUuid();
        captchaRequest.Vid = context.getVid();
        captchaRequest.Hostname = context.getHostname();
        captchaRequest.catpchaType = pxConfiguration.getCaptchaProvider();
        return captchaRequest;
    }

}
