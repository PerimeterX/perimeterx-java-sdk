package com.perimeterx.models.activities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.risk.BlockReason;
import com.perimeterx.models.risk.CustomParameters;
import com.perimeterx.utils.Constants;
import lombok.Getter;

/**
 * BlockActivityDetails model
 * <p>
 * Created by shikloshi on 06/07/2016.
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class BlockActivityDetails extends CommonActivityDetails {

    @JsonProperty("block_score")
    private int blockScore;

    @JsonProperty("block_reason")
    private BlockReason blockReason;

    @JsonProperty("client_uuid")
    private String blockUuid;

    @JsonProperty("http_method")
    private String httpMethod;

    @JsonProperty("http_version")
    private String httpVersion;

    @JsonProperty("px_cookie")
    private String pxCookie;

    @JsonProperty("risk_rtt")
    private long riskRtt;

    @JsonProperty("module_version")
    private String moduleVersion;

    @JsonProperty("cookie_origin")
    private String cookieOrigin;

    @JsonProperty("simulated_block")
    private Boolean simulatedBlock;

    @JsonUnwrapped
    private CustomParameters customParameters;

    @JsonProperty("block_action")
    private String blockAction;

    public BlockActivityDetails(PXContext context) {
        super(context);
        this.blockScore = context.getRiskScore();
        this.blockReason = context.getBlockReason();
        this.blockUuid = context.getUuid();
        this.httpMethod = context.getHttpMethod();
        this.httpVersion = context.getHttpVersion();
        this.pxCookie = context.getRiskCookie();
        this.riskRtt = context.getRiskRtt();
        this.cookieOrigin = context.getCookieOrigin();
        this.moduleVersion = Constants.SDK_VERSION;
        this.simulatedBlock = context.isMonitoredRequest();
        this.customParameters = context.getCustomParameters();

        if (context.getBlockAction() != null) {
            this.blockAction = context.getBlockAction().getCode();
        }
    }
}
