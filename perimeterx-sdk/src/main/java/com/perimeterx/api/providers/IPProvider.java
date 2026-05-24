package com.perimeterx.api.providers;

import javax.servlet.http.HttpServletRequest;

/**
 * Extract the IP address from request
 * <p>
 * Created by shikloshi on 05/07/2016.
 */
public interface IPProvider {

    /**
     * Extract IP from Http request
     *
     * @param request - from which we want to extract IP
     * @return - IP string
     */
    String getRequestIP(HttpServletRequest request);

}
