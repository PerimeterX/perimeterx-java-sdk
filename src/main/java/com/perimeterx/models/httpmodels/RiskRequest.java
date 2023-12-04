package com.perimeterx.models.httpmodels;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.risk.Request;
import com.perimeterx.models.risk.S2SCallReason;

/**
 * RiskRequest model
 * <p>
 * Created by shikloshi on 04/07/2016.
 */
public class RiskRequest {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("client_uuid")
    private String clientUuid;

    @JsonProperty("request")
    public Request request;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("vid")
    public String vid;

    @JsonProperty("additional")
    public Additional additional;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("pxhd")
    public String pxhd;
    @JsonProperty("cookie_origin")
    private String cookieOrigin;


    public static RiskRequest fromContext(PXContext context) {
        RiskRequest riskRequest = new RiskRequest();
        riskRequest.request = com.perimeterx.models.risk.Request.fromContext(context);
        riskRequest.vid = context.getVid();
        riskRequest.pxhd = context.getPxhd();
        riskRequest.cookieOrigin = context.getCookieOrigin();
        riskRequest.pxhd = context.getPxhd();
        riskRequest.clientUuid = context.getUuid();
        riskRequest.additional = com.perimeterx.models.httpmodels.Additional.fromContext(context);

        if (riskRequest.pxhd != null && "no_cookie".equals(riskRequest.additional.callReason)) {
            riskRequest.additional.callReason = S2SCallReason.NO_COOKIE_W_VID.getValue();
        }
        return riskRequest;
    }
}
