package com.perimeterx.api.blockhandler;

import com.perimeterx.api.blockhandler.templates.TemplateFactory;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.exceptions.PXException;
import com.perimeterx.utils.BlockAction;

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
        String pageTemplate = "block.mustache";
        if (context.getBlockAction().equals(BlockAction.CAPTCHA)) {
            String fileName = pxConfig.getCaptchaProvider().name().toLowerCase();
            String ext = ".mustache";
            pageTemplate = fileName + ext;
        }
        String page = TemplateFactory.getTemplate(context, pxConfig, pageTemplate);
        responseWrapper.setStatus(HttpServletResponse.SC_FORBIDDEN);
        responseWrapper.setContentType("text/html");
        try {
            responseWrapper.getWriter().print(page);
        } catch (IOException e) {
            throw new PXException(e);
        }
    }
}
