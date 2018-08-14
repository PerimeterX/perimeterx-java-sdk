package com.perimeterx.internals.cookie;

import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.utils.Constants;

/**
 * Created by nitzangoldfeder on 13/04/2017.
 */
public abstract class PXCookieFactory {

    public static AbstractPXCookie create(PXConfiguration pxConfiguration, CookieData cookieData) {
        switch (cookieData.getCookieVersion()) {
            case Constants.COOKIE_V1_KEY:
                return new PXCookieV1(pxConfiguration, cookieData);
            case Constants.COOKIE_V3_KEY:
                return new PXCookieV3(pxConfiguration, cookieData);
        }
        return null;
    }
}
