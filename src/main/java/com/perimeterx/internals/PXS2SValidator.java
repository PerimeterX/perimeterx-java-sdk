package com.perimeterx.internals;

import com.perimeterx.http.PXClient;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.exceptions.PXException;
import com.perimeterx.models.httpmodels.RiskRequest;
import com.perimeterx.models.httpmodels.RiskResponse;
import com.perimeterx.models.risk.BlockReason;
import com.perimeterx.models.risk.PassReason;
import com.perimeterx.utils.Constants;
import org.apache.http.conn.ConnectTimeoutException;

/**
 * High level Abstracted interface for calling PerimeterX servers
 * <p>
 * Created by shikloshi on 04/07/2016.
 */
public class PXS2SValidator {

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
     * @throws PXException
     */
    public boolean verify(PXContext pxContext) throws PXException {
        RiskResponse response;
        long startRiskRtt = System.currentTimeMillis();
        try {
            RiskRequest request = RiskRequest.fromContext(pxContext);
            response = pxClient.riskApiCall(request);
            pxContext.setMadeS2SApiCall(true);
            if (response == null) {
                // Error from PX service
                pxContext.setRiskRtt(System.currentTimeMillis() - startRiskRtt);
                pxContext.setPassReason(PassReason.ERROR);
                return true;
            }
            pxContext.setRiskScore(response.getScore());
            pxContext.setUuid(response.getUuid());
            pxContext.setBlockAction(response.getAction());

            if (pxContext.getRiskScore() < pxConfiguration.getBlockingScore()) {
                pxContext.setPassReason(PassReason.S2S);
                return true;
            } else if (response.getAction().equals(Constants.BLOCK_ACTION_CHALLENGE) && response.getActionData() != null && response.getActionData().getBody() != null) {
                pxContext.setBlockActionData(response.getActionData().getBody());
                pxContext.setBlockReason(BlockReason.CHALLENGE);
            } else {
                pxContext.setBlockReason(BlockReason.SERVER);
            }
            return false;
        } catch (ConnectTimeoutException e) {
            // Timeout handling - report pass reason and proceed with request
            pxContext.setPassReason(PassReason.S2S_TIMEOUT);
            return true;
        } catch (Exception e) {
            pxContext.setRiskRtt(System.currentTimeMillis() - startRiskRtt);
            pxContext.setPassReason(PassReason.ERROR);
            throw new PXException(e);
        } finally {
            pxContext.setRiskRtt(System.currentTimeMillis() - startRiskRtt);
        }
    }
}
