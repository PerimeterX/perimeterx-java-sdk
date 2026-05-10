package com.perimeterx.api.verificationhandler;

import com.perimeterx.models.PXContext;
import com.perimeterx.models.exceptions.PXException;

import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;

/**
 * Interface for handling verification after PerimeterX prepareProxyRequest finished analyzing the request.
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
    boolean handleVerification(PXContext context, HttpServletResponseWrapper responseWrapper) throws PXException, IOException;
}
