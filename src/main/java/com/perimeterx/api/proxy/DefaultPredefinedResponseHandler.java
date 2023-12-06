package com.perimeterx.api.proxy;

import com.perimeterx.api.PerimeterX;
import com.perimeterx.models.proxy.PredefinedResponse;
import com.perimeterx.utils.logger.IPXLogger;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DefaultPredefinedResponseHandler implements PredefinedResponseHelper {
    private static final IPXLogger logger = PerimeterX.globalLogger;

    @Override
    public void handlePredefinedResponse(HttpServletResponse res, PredefinedResponse predefinedResponse) {
        try {
            res.setHeader(HttpHeaders.CONTENT_TYPE, predefinedResponse.getContentType());
            res.setStatus(HttpStatus.SC_OK);
            res.getWriter().print(predefinedResponse.getContent());
        } catch (IOException e) {
            logger.error("Failed to render predefined response content {}, content-type: {}", predefinedResponse.getContent(), predefinedResponse.getContentType());
        }

    }
}
