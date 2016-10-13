package com.perimeterx.internals;

import com.perimeterx.http.PXClient;
import com.perimeterx.models.exceptions.PXException;
import com.perimeterx.models.httpmodels.RiskRequest;
import com.perimeterx.models.httpmodels.RiskResponse;

import java.io.IOException;

/**
 * High level Abstracted interface for calling PerimeterX servers
 * <p>
 * Created by shikloshi on 04/07/2016.
 */
public class PXS2SValidator {

    private PXClient pxClient;

    public PXS2SValidator(PXClient pxClient) {
        this.pxClient = pxClient;
    }

    /**
     * Verify if request is valid or not
     *
     * @param request - request per context for querying server
     * @return risk response from PX servers
     * @throws PXException
     */
    public RiskResponse verify(RiskRequest request) throws PXException {
        RiskResponse response;
        try {
            response = pxClient.riskApiCall(request);
        } catch (IOException e) {
            throw new PXException(e);
        }
        return response;
    }
}
