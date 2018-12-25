package com.perimeterx.models.httpmodels;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Risk API server Response POJO
 * <p>
 * Created by Shikloshi on 04/07/2016.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
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
    private String pxhd;


}
