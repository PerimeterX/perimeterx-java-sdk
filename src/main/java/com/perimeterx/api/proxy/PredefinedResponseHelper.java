package com.perimeterx.api.proxy;

import com.perimeterx.models.proxy.PredefinedResponse;

import javax.servlet.http.HttpServletResponse;

public interface PredefinedResponseHelper {
    void handlePredefinedResponse(HttpServletResponse res, PredefinedResponse predefinedResponse);
}
