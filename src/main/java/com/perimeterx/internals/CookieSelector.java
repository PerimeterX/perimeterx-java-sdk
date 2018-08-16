package com.perimeterx.internals;

import com.perimeterx.internals.cookie.AbstractPXCookie;
import com.perimeterx.internals.cookie.CookieData;
import com.perimeterx.internals.cookie.PXCookieFactory;
import com.perimeterx.internals.cookie.RawCookieData;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.exceptions.PXCookieDecryptionException;
import com.perimeterx.models.exceptions.PXException;
import com.perimeterx.models.risk.S2SCallReason;
import com.perimeterx.utils.PXLogger;
import org.apache.commons.lang3.StringUtils;

import java.util.List;


public class CookieSelector {

    private static final PXLogger logger = PXLogger.getLogger(CookieSelector.class);

    /**
     * @param context - This context should contain the authorization header cookie and/or tokens header
     * @param pxConfiguration - This context should contain the PxConfiguration class that the perimeterX object was initiated with
     * */
    public static AbstractPXCookie selectFromTokens(PXContext context, PXConfiguration pxConfiguration) {
        AbstractPXCookie result = null;
        S2SCallReason s2SCallReason = S2SCallReason.NO_COOKIE;
        List <RawCookieData> tokens = context.getTokens();
        String cookieOrig = null;
        if (tokens != null){
            for (RawCookieData token : tokens){
                String cookie = token.getSelectedCookie();
                String version = token.getVersion();
                cookieOrig = cookie;
                AbstractPXCookie selectedCookie = buildPxCookie(context, pxConfiguration, cookie, version);
                s2SCallReason = evaluateCookie(selectedCookie);
                if (S2SCallReason.NONE == s2SCallReason){
                    result = selectedCookie;
                    break;
                }
            }
        }
        if (result == null && context.getPxCookieOrig() == null){
            context.setPxCookieOrig(cookieOrig);
        }
        context.setS2sCallReason(s2SCallReason.name());
        return result;
    }


    /**
     * @param context - This context should contain the cookie header
     * @param pxConfiguration - This context should contain the PxConfiguration class that the perimeterX object was initiated with
     * */
    public static AbstractPXCookie selectOriginalTokens(PXContext context, PXConfiguration pxConfiguration) throws PXCookieDecryptionException, PXException {
        AbstractPXCookie result = null;
        String errorMessage = null;
        List <RawCookieData> tokens = context.getOriginalTokens();
        String cookieOrig = null;
        if (tokens != null){
            for (RawCookieData token : tokens){
                String cookie = token.getSelectedCookie();
                String version = token.getVersion();
                AbstractPXCookie selectedCookie = buildPxCookie(context, pxConfiguration, cookie, version);
                errorMessage = evaluateOriginalTokenCookie(selectedCookie);
                if(StringUtils.isEmpty(errorMessage)){
                    result = selectedCookie;
                    break;
                }
            }
        }
        context.setPxCookieOrig(cookieOrig);
        context.setOriginalTokenError(errorMessage);
        return result;
    }

    private static S2SCallReason evaluateCookie(AbstractPXCookie selectedCookie) {
        S2SCallReason s2SCallReason = S2SCallReason.NONE;
        if (selectedCookie == null) {
            logger.debug("Cookie is null");
            s2SCallReason = S2SCallReason.NO_COOKIE;
        }
        else {
            try {
                if (!selectedCookie.deserialize()) {
                    logger.debug("Cookie decryption failed, value: " + selectedCookie.getPxCookie());
                    s2SCallReason = S2SCallReason.INVALID_DECRYPTION;
                }
            } catch (PXCookieDecryptionException e) {
                logger.debug("Cookie decryption failed, value: " + selectedCookie.getPxCookie());
                s2SCallReason = S2SCallReason.INVALID_DECRYPTION;
            }
        }
        return s2SCallReason;
    }

    private static String evaluateOriginalTokenCookie(AbstractPXCookie selectedCookie) throws PXCookieDecryptionException {
        String error = "";
        if (selectedCookie == null ) {
            logger.debug("Original token is null");
            error =  "original_token_missing";
        }
        else if (!selectedCookie.deserialize()) {
            logger.debug("Original token decryption failed, value: " + selectedCookie.getPxCookie());
            error =  "decryption_failed";
        }
        return error;
    }

    private static AbstractPXCookie buildPxCookie(PXContext context, PXConfiguration pxConfiguration, String cookie, String cookieVersion) {

        CookieData cookieData = CookieData.builder().ip(context.getIp())
                .mobileToken(context.isMobileToken())
                .pxCookie(cookie)
                .cookieOrig(cookie)
                .userAgent(context.getUserAgent())
                .cookieVersion(cookieVersion)
                .build();
        AbstractPXCookie selectedCookie = PXCookieFactory.create(pxConfiguration,cookieData);
        logger.debug("Cookie found, Evaluating");


        return selectedCookie;
    }

}

