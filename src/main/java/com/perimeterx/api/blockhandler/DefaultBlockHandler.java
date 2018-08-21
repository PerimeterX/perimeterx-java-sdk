package com.perimeterx.api.blockhandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.perimeterx.api.blockhandler.templates.TemplateFactory;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.exceptions.PXException;
import com.perimeterx.models.httpmodels.MobilePageResponse;
import com.perimeterx.utils.Base64;
import com.perimeterx.utils.BlockAction;
import com.perimeterx.utils.Constants;

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
        String blockPage = getPage(context, pxConfig);
        String action;

        if (context.isMobileToken()) {
            action = (context.getBlockAction().equals(BlockAction.CAPTCHA)) ? Constants.MOBILE_ACTION_CAPTCHA : Constants.MOBILE_ACTION_BLOCK;
            String base64Page = Base64.encodeToString(blockPage.getBytes(), false);
            try {
                blockPage = new ObjectMapper().writeValueAsString(new MobilePageResponse(action, context.getUuid(), context.getAppId(), base64Page, context.getCollectorURL()));
            } catch (JsonProcessingException e) {
                throw new PXException(e);
            }
        }
        responseWrapper.setStatus(HttpServletResponse.SC_FORBIDDEN);
        responseWrapper.setContentType(context.isMobileToken() ? Constants.CONTENT_TYPE_APPLICATION_JSON : Constants.CONTENT_TYPE_TEXT_HTML);
        try {
            responseWrapper.getWriter().print(blockPage);
        } catch (IOException e) {
            throw new PXException(e);
        }
    }

    private String getPage(PXContext context, PXConfiguration pxConfig) throws PXException {
        String filePrefix = Constants.FILE_NAME_BLOCK;
        String fileSuffix = "";
        String template;

        if (context.getBlockAction().equals(BlockAction.CAPTCHA)) {
            filePrefix = Constants.CAPTCHA_BLOCK;
        }

        if (context.isMobileToken()) {
            fileSuffix = Constants.FILE_NAME_MOBILE;
        }

        template = filePrefix + fileSuffix + Constants.FILE_EXTENSION_MUSTACHE;
        return TemplateFactory.getTemplate(context, pxConfig, template);
    }
}
