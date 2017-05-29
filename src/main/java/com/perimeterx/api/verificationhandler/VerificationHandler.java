package com.perimeterx.api.verificationhandler;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.risk.BlockReason;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * An interface that describes how to handle the verification after the PerimeterX finished analyzing the request
 * Created by nitzangoldfeder on 28/05/2017.
 */
public interface VerificationHandler {
    /**
     * A Method that handles the verification after PerimeterX finished its processing
     * @param context
     * @param responseWrapper
     * @param blockReason
     * @return
     * @throws Exception
     */
    boolean handleVerification(PXContext context, HttpServletResponseWrapper responseWrapper, BlockReason blockReason) throws Exception;
}