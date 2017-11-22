package com.perimeterx.api.blockhandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.perimeterx.api.blockhandler.templates.TemplateFactory;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.exceptions.PXException;
import com.perimeterx.utils.Base64;
import com.perimeterx.utils.BlockAction;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;

import static com.perimeterx.utils.Constants.*;

/**
 * Default blocking implementation - Sends 403
 * <p>
 * Created by shikloshi on 03/07/2016.
 */
public class DefaultBlockHandler implements BlockHandler {

    public void handleBlocking(PXContext context, PXConfiguration pxConfig, HttpServletResponseWrapper responseWrapper) throws PXException {
        String response = getPage(context, pxConfig);

        if (context.isMobileToken()) {
            MobilePageResponse blockResponse = new MobilePageResponse(BLOCK_ACTION_CAPTCHA, context.getUuid(), context.getAppId(), Base64.decode(response).toString());
            try {
                response = new ObjectMapper().writeValueAsString(blockResponse);
            } catch (JsonProcessingException e) {
                throw new PXException(e);
            }
        }

        responseWrapper.setStatus(HttpServletResponse.SC_FORBIDDEN);
        responseWrapper.setContentType(context.isMobileToken() ? CONTENT_TYPE_APPLICATION_JSON : CONTENT_TYPE_TEXT_HTML);

        try {
            responseWrapper.getWriter().print(response);
        } catch (IOException e) {
            throw new PXException(e);
        }
    }

    private String getPage(PXContext context, PXConfiguration pxConfig) throws PXException {
        String pageTemplate = "block.mustache";
        if (context.getBlockAction().equals(BlockAction.CAPTCHA)) {
            String fileName = pxConfig.getCaptchaProvider().name().toLowerCase();
            String ext = ".mustache";
            pageTemplate = fileName + ext;
        }
        return TemplateFactory.getTemplate(context, pxConfig, pageTemplate);
    }
}
