package com.perimeterx.api.proxy.mock;

import com.perimeterx.api.proxy.ReverseProxy;
import com.perimeterx.models.PXContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.net.URISyntaxException;

public class NeverReverseProxy implements ReverseProxy {
    public static NeverReverseProxy instance;

    public static NeverReverseProxy getInstance() {
        if (instance == null) {
            instance = new NeverReverseProxy();
        }
        return instance;
    }

    private NeverReverseProxy() {
    }

    @Override
    public boolean reversePxClient(HttpServletRequest req, HttpServletResponse res, PXContext context) throws URISyntaxException, IOException {
        return false;
    }

    @Override
    public boolean reversePxXhr(HttpServletRequest req, HttpServletResponse res, PXContext context) throws URISyntaxException, IOException {
        return false;
    }

    @Override
    public boolean reverseCaptcha(HttpServletRequest req, HttpServletResponseWrapper res, PXContext context) throws IOException, URISyntaxException {
        return false;
    }
}
