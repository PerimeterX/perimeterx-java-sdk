package com.perimeterx.api.ip;

import javax.servlet.http.HttpServletRequest;

/**
 * Default IP Provider that look on the request remote address
 * <p>
 * Created by shikloshi on 05/07/2016.
 */
public class RemoteAddressIPProvider implements IPProvider {

    @Override
    public String getRequestIP(HttpServletRequest request) {
        return request.getRemoteAddr();
    }
}
