package com.perimeterx.api.activities;

import com.perimeterx.models.PXContext;
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
     */
    void handleBlockActivity(PXContext context) throws PXException;

    /**
     * Sends PageRequested Activity upon the valid request
     *
     * @param context - request context
     */
    void handlePageRequestedActivity(PXContext context) throws PXException;
}
