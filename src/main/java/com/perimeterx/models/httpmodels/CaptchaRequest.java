package com.perimeterx.models.httpmodels;

import com.perimeterx.models.PXContext;
import com.perimeterx.models.risk.Request;

/**
 * CaptchaRequest model
 * <p>
 * Created by shikloshi on 07/07/2016.
 */
public class CaptchaRequest {

    private Request request;
    private String vid;
    private String pxCaptcha;
    private String hostname;

    public CaptchaRequest(PXContext context) {
        this.request = new Request(context);
        this.pxCaptcha = context.getPxCaptcha();
        this.vid = context.getVid();
        this.hostname = context.getHostname();
    }

    public Request getRequest() {
        return request;
    }

    public String getVid() {
        return vid;
    }

    public String getPxCaptcha() {
        return pxCaptcha;
    }

    public String getHostname() {
        return hostname;
    }
}
