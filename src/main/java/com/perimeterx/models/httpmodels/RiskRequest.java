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

    private Request request;
    @JsonProperty("s2s_call_reason")
    private S2SCallReason callReason;

    public RiskRequest(PXContext context) {
        this.request = new Request(context);
        this.callReason = context.getS2sCallReason();
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public S2SCallReason getCallReason() {
        return callReason;
    }

    public void setCallReason(S2SCallReason callReason) {
        this.callReason = callReason;
    }
}
