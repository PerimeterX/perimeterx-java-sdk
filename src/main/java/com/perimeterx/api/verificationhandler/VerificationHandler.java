package com.perimeterx.api.verificationhandler;

import com.perimeterx.models.PXContext;

import javax.servlet.http.HttpServletResponseWrapper;

/**
 * Interface for handling verification after PerimeterX service finished analyzing the request.
 * Created by nitzangoldfeder on 28/05/2017.
 */
public interface VerificationHandler {
    /**
     * A Method that handles the verification after PerimeterX finished its processing
     *
     * @param context
     * @param responseWrapper
     * @return
     * @throws Exception
     */
    boolean handleVerification(PXContext context, HttpServletResponseWrapper responseWrapper) throws Exception;
}
