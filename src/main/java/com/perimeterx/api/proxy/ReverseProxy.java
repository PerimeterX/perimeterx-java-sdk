package com.perimeterx.api.proxy;

import com.perimeterx.models.PXContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by nitzangoldfeder on 14/05/2018.
 */
public interface ReverseProxy {
    /**
     * This method will reverse the request and fetch the client from PerimeterX backends
     * if return false, the module will continue to inspect the request
     * There must be a condition to redirect the request, it much match a certain prefix with combination of the appid and path
     *
     * @param req     - Request object
     * @param res     - Response object
     * @param context
     * @return true if response was handled, false otherwise
     * @throws URISyntaxException, IOException
     */
    boolean reversePxClient(HttpServletRequest req, HttpServletResponse res, PXContext context) throws URISyntaxException, IOException;

    /**
     * This method will reverse the request and send any XHR request back to PerimeterX backends
     * if return false, the module will continue to inspect the request
     * There must be a condition to redirect the request, it much match a certain prefix with combination of the appid and path
     *
     * @param req     - Request object
     * @param res     - Response object
     * @param context
     * @return true if response was handled, false otherwise
     * @throws URISyntaxException, IOException
     */
    boolean reversePxXhr(HttpServletRequest req, HttpServletResponse res, PXContext context) throws URISyntaxException, IOException;

    boolean reverseCaptcha(HttpServletRequest req, HttpServletResponseWrapper res, PXContext context) throws IOException, URISyntaxException;
}
