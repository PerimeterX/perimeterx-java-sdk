package com.perimeterx.models.httpmodels;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Risk API server Response POJO
 * <p>
 * Created by Shikloshi on 04/07/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RiskResponse {

    private String uuid;
    private int status;
    private int score;
    private String action;
    @JsonProperty("action_data")
    private RiskResponseBody actionData;
    @JsonProperty("data_enrichment")
    private JsonNode dataEnrichment;

    public RiskResponse() {
    }

    public RiskResponse(String uuid, int status, int score, String action,
                        RiskResponseBody actionData, JsonNode dataEnrichment) {
        this.uuid = uuid;
        this.status = status;
        this.score = score;
        this.action = action;
        this.actionData = actionData;
        this.dataEnrichment = dataEnrichment;
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

    public RiskResponseBody getActionData() {
        return actionData;
    }

    public void setActionData(RiskResponseBody actionData) {
        this.actionData = actionData;
    }

    public JsonNode getDataEnrichment() {
        return this.dataEnrichment;
    }
}
