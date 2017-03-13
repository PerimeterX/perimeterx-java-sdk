package com.perimeterx.api.blockhandler;

import com.perimeterx.api.PXConfiguration;
import com.perimeterx.api.blockhandler.templates.TemplateFactory;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.exceptions.PXException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;

/**
 * Default captcha block handler which display captcha page on block
 * <p>
 * Created by shikloshi on 07/07/2016.
 */
public class CaptchaBlockHandler implements BlockHandler {

    @Override
    public void handleBlocking(PXContext context, PXConfiguration pxConfig, HttpServletResponseWrapper responseWrapper) throws PXException {
        String page = TemplateFactory.getTemplate(context, pxConfig, "captcha.mustache");
        responseWrapper.setStatus(HttpServletResponse.SC_FORBIDDEN);
        responseWrapper.setContentType("text/html");
        try {
            responseWrapper.getWriter().print(page);
        } catch (IOException e) {
            throw new PXException(e);
        }
    }
}
