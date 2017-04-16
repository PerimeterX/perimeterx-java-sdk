package com.perimeterx.models.httpmodels;

/**
 * Risk API server Response POJO
 * <p>
 * Created by Shikloshi on 04/07/2016.
 */

//{"status":0,"uuid":"bb319090-2282-11e7-ba72-5d47483a3678","score":0,"action":"c"}
public class RiskResponse {

    private String uuid;
    private int status;
    private int score;
    private String action;

    public RiskResponse() {
    }

    public RiskResponse(String uuid, int status, int score, String action) {
        this.uuid = uuid;
        this.status = status;
        this.score = score;
        this.action = action;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
