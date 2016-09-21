package com.perimeterx.models.httpmodels;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.perimeterx.internals.cookie.RiskCookie;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.risk.Request;

/**
 * RiskRequest model
 * <p>
 * Created by shikloshi on 04/07/2016.
 */
public class RiskRequest {

    @JsonProperty("request")
    public Request Request;
    @JsonProperty("vid")
    public String Vid;
    @JsonProperty("additional")
    public Additional Additional;

    public static RiskRequest fromContext(PXContext context) {
        RiskRequest riskRequest = new RiskRequest();
        riskRequest.Request = com.perimeterx.models.risk.Request.fromContext(context);
        riskRequest.Vid = context.getVid();
        riskRequest.Additional = com.perimeterx.models.httpmodels.Additional.fromContext(context);
        return riskRequest;
    }
}
