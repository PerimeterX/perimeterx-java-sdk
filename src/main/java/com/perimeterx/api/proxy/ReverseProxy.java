package com.perimeterx.api.proxy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * Created by nitzangoldfeder on 14/05/2018.
 */
public interface ReverseProxy {
    boolean reversePxClient(HttpServletRequest req, HttpServletResponseWrapper res) throws Exception;
    boolean reversePxXhr(HttpServletRequest req, HttpServletResponseWrapper res) throws Exception;
}
