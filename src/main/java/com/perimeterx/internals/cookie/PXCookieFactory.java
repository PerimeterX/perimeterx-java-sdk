package com.perimeterx.internals.cookie;

import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.exceptions.PXException;
import com.perimeterx.utils.Constants;

import java.util.Set;

/**
 * Created by nitzangoldfeder on 13/04/2017.
 */
public abstract class PXCookieFactory {

    public static AbstractPXCookie create(PXConfiguration pxConfiguration, CookieData cookieData) throws PXException {
        // Return null if no cookies
        Set<String> cookieKeys = cookieData.getPxCookies().keySet();
        if (cookieKeys.isEmpty()) {
            return null;
        }

        // Will get the higher cookie because keys are sorted as a ordered set
        String cookieType = cookieKeys.iterator().next();
        switch (cookieType) {
            case Constants.COOKIE_V1_KEY:
                return new PXCookieV1(pxConfiguration, cookieData);
            case Constants.COOKIE_V3_KEY:
                return new PXCookieV3(pxConfiguration, cookieData);
        }
        return null;
    }
}
