package com.perimeterx.models.httpmodels;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.perimeterx.models.risk.Scores;

/**
 * Risk API server Response POJO
 * <p>
 * Created by Shikloshi on 04/07/2016.
 */
public class RiskResponse {

    private String uuid;
    private int status;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Scores scores = new Scores(0, -1, 0);
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;

    public RiskResponse() {
    }

    public RiskResponse(String uuid, int status, Scores scores, String message) {
        this.uuid = uuid;
        this.status = status;
        this.scores = scores;
        this.message = message;
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

    public Scores getScores() {
        return scores;
    }

    public void setScores(Scores scores) {
        this.scores = scores;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
