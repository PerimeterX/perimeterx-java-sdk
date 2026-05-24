package com.perimeterx.api.providers;

import javax.servlet.http.HttpServletRequest;

/**
 * IP Provider by specific header
 * <p>
 * Created by shikloshi on 05/07/2016.
 */
public class IPByHeaderProvider implements IPProvider {

    private String ipHeaderKey;

    public IPByHeaderProvider(String ipHeaderKey) {
        this.ipHeaderKey = ipHeaderKey;
    }

    @Override
    public String getRequestIP(HttpServletRequest request) {
        return request.getHeader(ipHeaderKey);
    }
}
