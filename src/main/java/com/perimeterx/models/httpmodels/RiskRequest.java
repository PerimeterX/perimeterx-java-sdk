package com.perimeterx.models.httpmodels;

import com.perimeterx.models.PXContext;
import com.perimeterx.models.risk.Request;

/**
 * RiskRequest model
 * <p>
 * Created by shikloshi on 04/07/2016.
 */
public class RiskRequest {

    private Request request;
    private String vid;
    private Additional additional;

    public RiskRequest(PXContext context) {
        this.request = new Request(context);
        this.vid = context.getVid();
        this.additional = new Additional(context);
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public Additional getAdditional() {
        return additional;
    }

    public void setAdditional(Additional additional) {
        this.additional = additional;
    }
}
