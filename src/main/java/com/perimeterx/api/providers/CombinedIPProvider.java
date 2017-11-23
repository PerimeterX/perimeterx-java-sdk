package com.perimeterx.api.providers;

import com.perimeterx.models.configuration.PXConfiguration;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by nitzangoldfeder on 06/11/2017.
 */
public class CombinedIPProvider implements IPProvider{
    private PXConfiguration pxConfiguration;

    public CombinedIPProvider(PXConfiguration pxConfiguration) {
        this.pxConfiguration = pxConfiguration;
    }

    @Override
    public String getRequestIP(HttpServletRequest request) {
        String ipHeader;

        for (String ip : pxConfiguration.getIpHeaders()) {
            ipHeader = request.getHeader(ip);
            if (ipHeader != null) {
                return ipHeader;
            }
        }
        return request.getRemoteAddr();
    }
}
