package com.perimeterx.api.blockhandler;

import com.perimeterx.models.PXContext;
import com.perimeterx.models.exceptions.PXException;

import javax.servlet.http.HttpServletResponseWrapper;

/**
 * BlockHandler is a common interface to be applied on block event
 * <p>
 * Created by Shikloshi on 03/07/2016.
 */
@FunctionalInterface
public interface BlockHandler {

    /**
     * Blocking handle will be called when pxVerify will return that user is not verified
     */
    void handleBlocking(PXContext context, HttpServletResponseWrapper responseWrapper) throws PXException;
}
