package com.perimeterx.api.blockhandler;

import com.perimeterx.api.PXConfiguration;
import com.perimeterx.api.blockhandler.templates.TemplateFactory;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.exceptions.PXException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;

/**
 * Default blocking implementation - Sends 403
 * <p>
 * Created by shikloshi on 03/07/2016.
 */
public class DefaultBlockHandler implements BlockHandler {

    public void handleBlocking(PXContext context, PXConfiguration pxConfig, HttpServletResponseWrapper responseWrapper) throws PXException {
        String page = TemplateFactory.getTemplate(context, pxConfig, "block.mustache");
        responseWrapper.setStatus(HttpServletResponse.SC_FORBIDDEN);
        responseWrapper.setContentType("text/html");
        try {
            responseWrapper.getWriter().print(page);
        } catch (IOException e) {
            throw new PXException(e);
        }
    }
}
