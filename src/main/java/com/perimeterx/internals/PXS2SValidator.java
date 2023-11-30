package com.perimeterx.internals;

import com.perimeterx.api.additionalContext.PXHDSource;
import com.perimeterx.http.PXClient;
import com.perimeterx.internals.cookie.DataEnrichmentCookie;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.exceptions.PXException;
import com.perimeterx.models.httpmodels.RiskResponse;
import com.perimeterx.models.risk.BlockReason;
import com.perimeterx.models.risk.PassReason;
import com.perimeterx.models.risk.S2SErrorReason;
import com.perimeterx.models.risk.S2SErrorReasonInfo;
import com.perimeterx.utils.Constants;
import com.perimeterx.utils.EnforcerErrorUtils;
import com.perimeterx.utils.logger.PXLogger;
import org.apache.http.conn.ConnectTimeoutException;

import static org.apache.commons.lang3.StringUtils.*;

/**
 * High level Abstracted interface for calling PerimeterX servers
 * <p>
 * Created by shikloshi on 04/07/2016.
 */
public class PXS2SValidator implements PXValidator {

    private static final PXLogger logger = PXLogger.getLogger(PXS2SValidator.class);

    private PXClient pxClient;
    private PXConfiguration pxConfiguration;

    public PXS2SValidator(PXClient pxClient, PXConfiguration pxConfiguration) {
        this.pxClient = pxClient;
        this.pxConfiguration = pxConfiguration;
    }

    /**
     * Verify if request is valid or not
     *
     * @param pxContext - Request context
     * @return risk response from PX servers
     * @throws PXException will be thrown when an error occurs
     */
    public boolean verify(PXContext pxContext) {
        logger.debug(PXLogger.LogReason.DEBUG_S2S_RISK_API_REQUEST, pxContext.getS2sCallReason());
        RiskResponse response = null;
        long startRiskRtt = System.currentTimeMillis();
        long rtt;

        try {
            response = pxClient.riskApiCall(pxContext);
        } catch (ConnectTimeoutException e) {
            // Timeout handling - report pass reason and proceed with request
            pxContext.setPassReason(PassReason.S2S_TIMEOUT);
            return true;
        } catch (Exception e) {
            handleS2SError(pxContext, System.currentTimeMillis() - startRiskRtt, response, e);
            logger.error("Error {}: {}", e.toString(), e.getStackTrace());
            return true;
        }

        try {
            rtt = System.currentTimeMillis() - startRiskRtt;
            logger.debug(PXLogger.LogReason.DEBUG_S2S_RISK_API_RESPONSE, (response == null) ? "" : response.getScore(), rtt);

            if (!isResponseValid(response)) {
                handleS2SError(pxContext, rtt, response, null);
                return true;
            }

            updateContextFromResponse(pxContext, response);

            if (pxContext.getRiskScore() < pxConfiguration.getBlockingScore()) {
                pxContext.setPassReason(PassReason.S2S);
                return true;
            } else if (response.getAction().equals(Constants.BLOCK_ACTION_CHALLENGE) && response.getActionData() != null && response.getActionData().getBody() != null) {
                pxContext.setBlockActionData(response.getActionData().getBody());
                pxContext.setBlockReason(BlockReason.CHALLENGE);
            } else {
                pxContext.setBlockReason(BlockReason.SERVER);
            }
            logger.debug(PXLogger.LogReason.DEBUG_S2S_ENFORCING_ACTION, pxContext.getBlockReason());
            return false;
        } catch (Exception e) {
            if (!pxContext.getS2sErrorReasonInfo().isErrorSet()) {
                String errorMessage = PXLogger.LogReason.ERROR_RISK_EVALUATION_EXCEPTION.toString();
                EnforcerErrorUtils.handleEnforcerError(pxContext, errorMessage, e);
            }
            logger.error("Error {}: {}", e.toString(), e.getStackTrace());
            return true;
        } finally {
            pxContext.setRiskRtt(System.currentTimeMillis() - startRiskRtt);
        }
    }

    private void updateContextFromResponse(PXContext pxContext, RiskResponse response) {
        pxContext.setRiskScore(response.getScore());
        pxContext.setUuid(response.getUuid());
        pxContext.setBlockAction(response.getAction());
        DataEnrichmentCookie dataEnrichment = new DataEnrichmentCookie(response.getDataEnrichment(), true);
        pxContext.setPxde(dataEnrichment.getJsonPayload());
        pxContext.setPxdeVerified(dataEnrichment.isValid());

        if(isNoneBlank(response.getPxhd())) {
            pxContext.setPxhd(response.getPxhd());
            pxContext.setPxhdSource(PXHDSource.RISK);
        }
        if(isNoneBlank(response.getPxhdDomain())) {
            pxContext.setPxhdDomain(response.getPxhdDomain());
        }
        if (response.getAdditionalRiskInfo() != null) {
            pxContext.setAdditionalRiskInfo(response.getAdditionalRiskInfo());
        }
    }

    private boolean isResponseValid(RiskResponse response) {
        return response != null && response.getStatus() == 0;
    }

    private void handleS2SError(PXContext pxContext, long rtt, RiskResponse response, Exception exception) {
        pxContext.setRiskRtt(rtt);
        pxContext.setPassReason(PassReason.S2S_ERROR);

        if (response != null && response.getUuid() != null) {
            pxContext.setUuid(response.getUuid());
        }

        if (!pxContext.getS2sErrorReasonInfo().isErrorSet()) {
            S2SErrorReason errorReason = getS2SErrorReason(pxContext, response);
            String errorMessage = getS2SErrorMessage(response, exception);
            pxContext.setS2sErrorReasonInfo(new S2SErrorReasonInfo(errorReason, errorMessage));
        }
    }

    private S2SErrorReason getS2SErrorReason(PXContext pxContext, RiskResponse response) {
        if (!pxContext.isMadeS2SApiCall()) {
            return S2SErrorReason.UNABLE_TO_SEND_REQUEST;
        } else if (response != null && !isResponseValid(response)) {
            return S2SErrorReason.REQUEST_FAILED_ON_SERVER;
        }
        return S2SErrorReason.UNKNOWN_ERROR;
    }

    private String getS2SErrorMessage(RiskResponse response, Exception exception) {
        if (exception != null) {
            return exception.toString();
        } else if (response != null && !isResponseValid(response)) {
            return response.getMessage();
        }
        int CURRENT_FUNCTION_INDEX = 1;
        return String.format("Error: %s - Response is %s",
                Thread.currentThread().getStackTrace()[CURRENT_FUNCTION_INDEX].toString(),
                response == null ? "null" : response.toString());
    }
}
