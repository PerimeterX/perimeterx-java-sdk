package com.perimeterx.api.blockhandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.perimeterx.api.blockhandler.templates.TemplateFactory;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.exceptions.PXException;
import com.perimeterx.models.httpmodels.AdvancedBlockingResponse;
import com.perimeterx.models.httpmodels.MobilePageResponse;
import com.perimeterx.utils.Base64;
import com.perimeterx.utils.BlockAction;
import com.perimeterx.utils.Constants;

import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Default blocking implementation - Sends 403
 * <p>
 * Created by shikloshi on 03/07/2016.
 */
public class DefaultBlockHandler implements BlockHandler {

    public void handleBlocking(PXContext context, PXConfiguration pxConfig, HttpServletResponseWrapper responseWrapper) throws PXException {
        Map<String, String> props = new HashMap<>();
        String filePrefix;
        String blockPageResponse;
        switch (context.getBlockAction()) {
            case RATE:
                filePrefix = Constants.RATELIMIT_TEMPLATE;
                blockPageResponse = getPage(props, filePrefix);
                break;

            case CHALLENGE:
                String actionData = context.getBlockActionData();
                if (actionData != null) {
                    blockPageResponse = actionData;
                    break;
                }
            default:
                filePrefix = Constants.CAPTCHA_BLOCK_TEMPLATE;
                props = TemplateFactory.getProps(context, pxConfig);
                blockPageResponse = getPage(props, filePrefix);
        }
        try {
            sendMessage(blockPageResponse, responseWrapper, context, pxConfig);
        } catch (IOException e) {
            throw new PXException(e);
        }
    }

    private void sendMessage(String blockPageResponse, HttpServletResponseWrapper responseWrapper, PXContext context, PXConfiguration pxConfig) throws PXException, IOException {
        if (context.getBlockAction() == BlockAction.RATE) {
            responseWrapper.setStatus(429);
        } else {
            responseWrapper.setStatus(403);
        }
        if (context.isMobileToken()) {
            responseWrapper.setContentType(Constants.CONTENT_TYPE_APPLICATION_JSON);
            String base64Page = Base64.encodeToString(blockPageResponse.getBytes(), false);
            try {
                blockPageResponse = new ObjectMapper().writeValueAsString(new MobilePageResponse(parseAction(context.getBlockAction().getCode()), context.getUuid(), context.getVid(), context.getAppId(), base64Page, context.getCollectorURL()));
            } catch (JsonProcessingException e) {
                throw new PXException(e);
            }
        } else {
            //handle advanced blocking mode
            if (shouldHandleAdvancedBlockingResponse(context)) {
                responseWrapper.setContentType(Constants.CONTENT_TYPE_APPLICATION_JSON);

                Map<String, String> props = TemplateFactory.getProps(context, pxConfig);
                AdvancedBlockingResponse advancedBlockingResponse = new AdvancedBlockingResponse(props.get("appId"),
                        props.get("jsClientSrc"),
                        props.get("firstPartyEnabled"),
                        props.get("vid"),
                        props.get("uuid"),
                        props.get("hostUrl"),
                        props.get("blockScript"),
                        props.get("altBlockScript"));

                blockPageResponse = new ObjectMapper().writeValueAsString(advancedBlockingResponse);
            } else {
                responseWrapper.setContentType(Constants.CONTENT_TYPE_TEXT_HTML);
            }
        }
        responseWrapper.getWriter().print(blockPageResponse);
    }

    private boolean shouldHandleAdvancedBlockingResponse(PXContext context) {

        //if advanced blocking response config is disabled
        if (!context.isAdvancedBlockingResponse()) {
            return false;
        }

        boolean headerExists;

        //otherwise check headers
        String header = context.getHeaders().get("accept");
        if (header != null && header.toLowerCase().contains("application/json")) {
            headerExists = true;
        } else {
            header = context.getHeaders().get("content-type");
            headerExists = header != null && header.toLowerCase().contains("application/json");
        }

        return headerExists
                && context.getCookieOrigin().equals(Constants.COOKIE_HEADER_NAME)
                && context.getBlockAction() != BlockAction.RATE;
    }

    private String parseAction(String code) {
        switch (code) {
            case "b":
                return "block";
            case "j":
                return "challenge";
            case "r":
                return "ratelimit";
            default:
                return "captcha";
        }
    }

    private String getPage(Map<String, String> props, String filePrefix) throws PXException {

        String template = filePrefix + Constants.FILE_EXTENSION_MUSTACHE;
        return TemplateFactory.getTemplate(template, props);
    }
}
