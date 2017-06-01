package com.perimeterx.internals;

import com.perimeterx.api.PXConfiguration;
import com.perimeterx.http.PXClient;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.exceptions.PXException;
import com.perimeterx.models.httpmodels.RiskRequest;
import com.perimeterx.models.httpmodels.RiskResponse;
import com.perimeterx.models.risk.BlockReason;
import com.perimeterx.models.risk.PassReason;
import org.apache.http.conn.ConnectTimeoutException;

import java.io.IOException;

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
            pxContext.setScore(response.getScore());
            pxContext.setUuid(response.getUuid());

            if (pxContext.getScore() <= pxConfiguration.getBlockingScore()){
                pxContext.setPassReason(PassReason.S2S);
                return true;
            }
            pxContext.setBlockReason(BlockReason.SERVER);
            return false;
        } catch (Exception e) {
            pxContext.setRiskRtt(System.currentTimeMillis() - startRiskRtt);
            //Handle timeout exception, else it a different error
            if (e instanceof ConnectTimeoutException){
                pxContext.setPassReason(PassReason.S2S_TIMEOUT);
                return true;
            }
            throw new PXException(e);
        }

    }
}
