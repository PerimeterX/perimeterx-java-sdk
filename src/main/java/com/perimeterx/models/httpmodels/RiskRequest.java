package com.perimeterx.models.httpmodels;

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

    @JsonProperty("request")
    public Request request;
    @JsonProperty("vid")
    public String vid;
    @JsonProperty("additional")
    public Additional additional;
    @JsonProperty("firstParty")
    public boolean firstParty;
    @JsonProperty("pxhd")
    public String pxhd;
    @JsonProperty("vid_source")
    public String vidSource;

    public static RiskRequest fromContext(PXContext context) {
        RiskRequest riskRequest = new RiskRequest();
        riskRequest.request = com.perimeterx.models.risk.Request.fromContext(context);
        riskRequest.vid = context.getVid();
        riskRequest.pxhd = context.getPxhd();
        riskRequest.vidSource = context.getVidSource().getValue();
        riskRequest.additional = com.perimeterx.models.httpmodels.Additional.fromContext(context);
        riskRequest.firstParty = context.getPxConfiguration().isFirstPartyEnabled();
        if (riskRequest.vid != null && riskRequest.pxhd != null && "no_cookie".equals(riskRequest.additional.callReason)) {
            riskRequest.additional.callReason = S2SCallReason.NO_COOKIE_W_VID.getValue();
        }
        return riskRequest;
    }
}
