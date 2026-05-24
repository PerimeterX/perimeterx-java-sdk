package com.perimeterx.api.providers;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by shikloshi on 02/02/2017.
 */
public interface HostnameProvider {

    String getHostname(HttpServletRequest req);
}
