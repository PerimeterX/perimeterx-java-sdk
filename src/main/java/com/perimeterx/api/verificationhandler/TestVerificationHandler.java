package com.perimeterx.api.verificationhandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.perimeterx.api.activities.ActivityHandler;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.exceptions.PXException;

import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;

public class TestVerificationHandler implements VerificationHandler {

    GsonBuilder builder = new GsonBuilder();
    Gson gson = builder.create();
    PXConfiguration pxConfig;
    VerificationHandler defaultVerificationHandler;

    public TestVerificationHandler(PXConfiguration pxConfig, ActivityHandler activityHandler) {
        this.pxConfig = pxConfig;
        this.defaultVerificationHandler = new DefaultVerificationHandler(pxConfig, activityHandler);
    }

    public boolean handleVerification(PXContext pxContext, HttpServletResponseWrapper httpServletResponseWrapper) throws PXException, IOException {
        httpServletResponseWrapper.setContentType("application/json");
        httpServletResponseWrapper.setStatus(200);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("cookie_origin", pxContext.getCookieOrigin());
        jsonObject.add("px_cookies", gson.toJsonTree(pxContext.getTokens()));
        jsonObject.addProperty("px_cookie_orig", pxContext.getPxCookieOrig());
        jsonObject.addProperty("decoded_px_cookie", pxContext.getRiskCookie());
        jsonObject.addProperty("px_cookie_hmac", pxContext.getCookieHmac());
        jsonObject.addProperty("px_captcha", pxContext.getPxCaptcha());
        jsonObject.addProperty("ip", pxContext.getIp());
        jsonObject.addProperty("http_version", pxContext.getHttpVersion());
        jsonObject.addProperty("http_method", pxContext.getHttpMethod());
        jsonObject.add("headers", gson.toJsonTree(pxContext.getHeaders()));
        jsonObject.addProperty("hostname", pxContext.getHostname());
        jsonObject.addProperty("uri", pxContext.getUri());
        jsonObject.addProperty("user_agent", pxContext.getUserAgent());
        jsonObject.addProperty("full_url", pxContext.getFullUrl());
        jsonObject.addProperty("s2s_call_reason", pxContext.getS2sCallReason());
        jsonObject.addProperty("score", pxContext.getRiskScore());
        jsonObject.addProperty("vid", pxContext.getVid());
        jsonObject.addProperty("block_reason", pxContext.getBlockReason().getValue());
        jsonObject.addProperty("pass_reason", pxContext.getPassReason().getValue());
        jsonObject.addProperty("risk_rtt", pxContext.getRiskRtt());
        jsonObject.addProperty("uuid", pxContext.getUuid());
        jsonObject.addProperty("is_made_s2s_api_call", pxContext.isMadeS2SApiCall());
        jsonObject.addProperty("block_action", pxContext.getBlockAction().name());
        jsonObject.addProperty("block_data", pxContext.getBlockActionData());
        jsonObject.addProperty("sensitive_route", pxContext.isSensitiveRoute());
        jsonObject.addProperty("sensitive_route_list", gson.toJson(pxConfig.getSensitiveRoutes()));
        jsonObject.addProperty("pxde", pxContext.getPxde().toString());
        httpServletResponseWrapper.getWriter().print(jsonObject);

        return false;
    }
}
