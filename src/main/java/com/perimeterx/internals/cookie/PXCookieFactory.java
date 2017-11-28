package com.perimeterx.internals.cookie;

import com.perimeterx.models.PXContext;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.exceptions.PXException;
import com.perimeterx.models.risk.S2SCallReason;
import com.perimeterx.utils.Constants;

import java.util.Set;

/**
 * Created by nitzangoldfeder on 13/04/2017.
 */
public abstract class PXCookieFactory {

    public static AbstractPXCookie create(PXConfiguration pxConfiguration, PXContext pxContext) throws PXException {
        // Return null if no cookies
        Set<String> cookieKeys = pxContext.getPxCookies().keySet();
        if (cookieKeys.isEmpty()) {
            //If there is a mobile header and no cookie value return decryption error cookie
            return pxContext.isMobileToken() ? new PXCookieError(S2SCallReason.INVALID_DECRYPTION.getValue()) : null;
        }

        // Will get the higher cookie because keys are sorted as a ordered set
        String cookieType = cookieKeys.iterator().next();
        switch (cookieType) {
            case Constants.COOKIE_V1_KEY_PREFIX:
                return new PXCookieV1(pxConfiguration, pxContext);
            case Constants.COOKIE_V3_KEY_PREFIX:
                return new PXCookieV3(pxConfiguration, pxContext);
            case Constants.MOBILE_ERROR_NO_COOKIE:
                return new PXCookieError(Constants.MOBILE_ERROR_NO_COOKIE);
            case Constants.MOBILE_ERROR_NO_CONNECTION:
                return new PXCookieError(Constants.MOBILE_ERROR_NO_CONNECTION);
            case Constants.MOBILE_ERROR_PINNING_PROBLEM:
                return new PXCookieError(Constants.MOBILE_ERROR_PINNING_PROBLEM);
            default:
                return null;
        }
    }
}
