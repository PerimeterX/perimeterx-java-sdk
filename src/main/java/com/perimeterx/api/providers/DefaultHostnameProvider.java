package com.perimeterx.api.providers;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by shikloshi on 02/02/2017.
 */
public class DefaultHostnameProvider implements HostnameProvider {

    @Override
    public String getHostname(HttpServletRequest req) {
        return req.getRemoteHost();
    }
}
