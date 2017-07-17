package com.perimeterx.internals.cookie;

import com.perimeterx.models.PXContext;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.exceptions.PXException;
import com.perimeterx.utils.Constants;

import java.util.Set;

/**
 * Created by nitzangoldfeder on 13/04/2017.
 */
public abstract class PXCookieFactory {

    public static DefaultPXCookie create(PXConfiguration pxConfiguration, PXContext pxContext) throws PXException {
        // Return null if no cookies
        Set<String> cookieKeys = pxContext.getPxCookies().keySet();
        if (cookieKeys.isEmpty()) {
            return null;
        }

        // Will get the higher cookie because keys are sorted as a ordered set
        String cookieType = cookieKeys.iterator().next();
        switch (cookieType) {
            case Constants.COOKIE_V1_KEY:
                return new PXCookieV1(pxConfiguration, pxContext);
            case Constants.COOKIE_V3_KEY:
                return new PXCookieV3(pxConfiguration, pxContext);
            default:
                return null;
        }
    }
}
