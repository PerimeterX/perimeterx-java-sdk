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

import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class CookieSelector {

    private static final PXLogger logger = PXLogger.getLogger(CookieSelector.class);

    /**
     * @param context - This context should contain the authorization header cookie and/or tokens header
     * @param pxConfiguration - This context should contain the PxConfiguration class that the perimeterX object was initiated with
     * */
    public static AbstractPXCookie selectFromTokens(PXContext context, PXConfiguration pxConfiguration) throws PXException {
        AbstractPXCookie result = null;
        S2SCallReason s2SCallReason = null;
        RawCookieData tokenCookie = context.getAuthCookie();
        RawCookieData multipleTokensCookie = context.getTokensCookie();
        if (multipleTokensCookie != null){
            context.setPxCookieOrig(multipleTokensCookie.getSelectedCookie());
            result = parseCookieFromCookieMap(context, pxConfiguration, multipleTokensCookie.getCookieMap(), multipleTokensCookie.getSelectedCookie());
        }
        if (result == null && tokenCookie != null){
            if (context.getPxCookieOrig() == null){
                context.setPxCookieOrig(tokenCookie.getSelectedCookie());
            }
            Set <String> cookieKeys = tokenCookie.getCookieMap().keySet();
            Iterator <String> versionsIter = cookieKeys.iterator();
            if (versionsIter.hasNext()){
                String cookieVersion = versionsIter.next();
                AbstractPXCookie selectedCookie = parseCookieFromCookieMap(context,pxConfiguration,tokenCookie.getCookieMap(),cookieVersion);
                s2SCallReason = evaluateCookie(selectedCookie);
                if (s2SCallReason == null){
                    result = selectedCookie;
                }
            }
        }
        else if (tokenCookie == null){
            context.setS2sCallReason(S2SCallReason.NO_COOKIE.name());
        }
        if (s2SCallReason != null){
            context.setS2sCallReason(s2SCallReason.name());
        }
        return result;
    }

    /**
     * @param context - This context should contain the cookie header
     * @param pxConfiguration - This context should contain the PxConfiguration class that the perimeterX object was initiated with
     * */
    public static AbstractPXCookie selectFromHeader(PXContext context, PXConfiguration pxConfiguration) throws PXCookieDecryptionException, PXException {
        RawCookieData cookierHeader = context.getHeaderCookie();
        AbstractPXCookie selectedCookie = null;
        if (cookierHeader != null){
            context.setPxCookieOrig(cookierHeader.getSelectedCookie());
            selectedCookie = parseCookieFromCookieMap(context, pxConfiguration, context.getHeaderCookie().getCookieMap(), context.getHeaderCookie().getSelectedCookie());
            S2SCallReason s2SCallReason = evaluateCookie(selectedCookie);
            if (s2SCallReason != null){
                context.setS2sCallReason(s2SCallReason.name());
            }
        }
        else {
            context.setS2sCallReason(S2SCallReason.NO_COOKIE.name());
        }
        return selectedCookie;
    }

    /**
     * @param context - This context should contain the cookie header
     * @param pxConfiguration - This context should contain the PxConfiguration class that the perimeterX object was initiated with
     * */
    public static AbstractPXCookie selectOriginalTokens(PXContext context, PXConfiguration pxConfiguration) throws PXCookieDecryptionException, PXException {
        AbstractPXCookie result = null;
        String errorMessage = null;
        RawCookieData originalTokenCookie = context.getOriginalTokenCookie();
        RawCookieData multipleOriginalTokens = context.getOriginalTokensCookie();
        if (multipleOriginalTokens != null){
            context.setOriginalToken(multipleOriginalTokens.getSelectedCookie());
            Map<String, String> cookieMap = multipleOriginalTokens.getCookieMap();
            Set<String> cookieKeys = cookieMap.keySet();
            AbstractPXCookie selectedCookie = null;
            for(String cookieVersion : cookieKeys){
                String cookie = cookieMap.get(cookieVersion);
                selectedCookie = buildPxCookie(context, pxConfiguration, cookie, multipleOriginalTokens.getSelectedCookie(), cookieVersion);
                errorMessage = evaluateOriginalTokenCookie(selectedCookie);
                if(StringUtils.isEmpty(errorMessage)){
                    result = selectedCookie;
                    break;
                }
            }
        }
        if (result == null && originalTokenCookie != null){
            context.setOriginalToken(originalTokenCookie.getSelectedCookie());
            Set<String> cookieKeys = originalTokenCookie.getCookieMap().keySet();
            Iterator<String> versionsIter = cookieKeys.iterator();
            if(versionsIter.hasNext()){
                String cookieVersion = cookieKeys.iterator().next();
                String cookie = originalTokenCookie.getCookieMap().get(cookieVersion);
                AbstractPXCookie selectedCookie = buildPxCookie(context, pxConfiguration, cookie, originalTokenCookie.getSelectedCookie(), cookieVersion);
                errorMessage = evaluateOriginalTokenCookie(selectedCookie);
                if(StringUtils.isEmpty(errorMessage)){
                    result = selectedCookie;
                }
            }
        }
        context.setOriginalTokenError(errorMessage);
        return result;
    }

    private static AbstractPXCookie parseCookieFromCookieMap(PXContext context, PXConfiguration pxConfiguration, Map<String, String> cookieMap, String cookieOrig) throws  PXException {
        Set<String> cookieKeys = cookieMap.keySet();
        AbstractPXCookie selectedCookie = null;
        for(String cookieKey : cookieKeys){
            try{
                String cookie = cookieMap.get(cookieKey);
                selectedCookie = buildPxCookie(context, pxConfiguration, cookie, cookieOrig, cookieKey);
                S2SCallReason s2SCallReason = evaluateCookie(selectedCookie);
                if(s2SCallReason == null){
                    return selectedCookie;
                }
            }
            catch (PXCookieDecryptionException e ){

            }

        }
        return selectedCookie;
    }

    private static S2SCallReason evaluateCookie(AbstractPXCookie selectedCookie) {
        if (selectedCookie == null) {
            logger.debug("Cookie is null");
            return S2SCallReason.NO_COOKIE;
        }
        else {
            try {
                if (!selectedCookie.deserialize()) {
                    logger.debug("Cookie decryption failed, value: " + selectedCookie.getPxCookie());
                    return S2SCallReason.INVALID_DECRYPTION;
                }
            } catch (PXCookieDecryptionException e) {
                logger.debug("Cookie decryption failed, value: " + selectedCookie.getPxCookie());
                return S2SCallReason.INVALID_DECRYPTION;
            }
        }
        return null;
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

    private static AbstractPXCookie buildPxCookie(PXContext context, PXConfiguration pxConfiguration, String cookie, String cookieOrig, String cookieVersion) throws PXException, PXCookieDecryptionException {

        CookieData cookieData = CookieData.builder().ip(context.getIp())
                .mobileToken(context.isMobileToken())
                .pxCookie(cookie)
                .userAgent(context.getUserAgent())
                .cookieOrig(cookieOrig)
                .cookieVersion(cookieVersion)
                .build();
        AbstractPXCookie selectedCookie = PXCookieFactory.create(pxConfiguration,cookieData);
        logger.debug("Cookie found, Evaluating");


        return selectedCookie;
    }

}

