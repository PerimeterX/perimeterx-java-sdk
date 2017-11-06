package com.perimeterx.models.httpmodels;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.configuration.CaptchaProvider;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.risk.Request;

import java.util.ArrayList;

/**
 * Created by nitzangoldfeder on 01/11/2017.
 */
public class CaptchaRequest extends Request {
    @JsonProperty("captchaType")
    public CaptchaProvider captchaType;

    public static Request fromContext(PXContext pxContext, PXConfiguration pxConfiguration) {
        CaptchaRequest requestCaptcha = new CaptchaRequest();
        requestCaptcha.captchaType = pxConfiguration.getCaptchaProvider();
        requestCaptcha.Headers = new ArrayList<>(pxContext.getHeaders().entrySet());
        requestCaptcha.IP = pxContext.getIp();
        requestCaptcha.URI = pxContext.getUri();
        requestCaptcha.URL = pxContext.getFullUrl();

        return requestCaptcha;
    }
}
