package com.perimeterx.models.activities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.risk.BlockReason;

/**
 * BlockActivityDetails model
 * <p>
 * Created by shikloshi on 06/07/2016.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class BlockActivityDetails implements ActivityDetails{

    @JsonProperty("block_score")
    private int blockScore;
    @JsonProperty("block_reason")
    private BlockReason blockReason;
    @JsonProperty("block_uuid")
    private String blockUuid;
    @JsonProperty("http_method")
    private String httpMethod;
    @JsonProperty("http_version")
    private String httpVersion;

    public BlockActivityDetails(PXContext context) {
        this.blockScore = context.getScore();
        this.blockReason = context.getBlockReason();
        this.blockUuid = context.getUuid();
        this.httpMethod = context.getHttpMethod();
        this.httpVersion = context.getHttpVersion();
    }

    public int getBlockScore() {
        return blockScore;
    }

    public BlockReason getBlockReason() {
        return blockReason;
    }

    public String getBlockUuid() {
        return blockUuid;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getHttpVersion() {
        return httpVersion;
    }
}
