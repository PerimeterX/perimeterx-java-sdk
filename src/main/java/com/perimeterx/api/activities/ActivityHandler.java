package com.perimeterx.api.activities;

import com.perimeterx.models.PXContext;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.exceptions.PXException;

/**
 * Sends Activity to PX servers according to request verification result
 * <p>
 * Created by shikloshi on 05/07/2016.
 */
public interface ActivityHandler {

    /**
     * Sends BlockActivity upon the request that was blocked
     *
     * @param context - request context
     * @throws PXException
     */
    void handleBlockActivity(PXContext context) throws PXException;

    /**
     * Sends PageRequested Activity upon the valid request
     *
     * @param context - request context
     * @throws PXException
     */
    void handlePageRequestedActivity(PXContext context) throws PXException;

    /**
     * Sends enforcer telemetry activity upon init/new configuration
     * @param pxConfig
     * @throws PXException
     */
    void handleEnforcerTelemetryActivity(PXConfiguration pxConfig) throws PXException;
}
