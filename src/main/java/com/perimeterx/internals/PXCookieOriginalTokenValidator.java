package com.perimeterx.internals;

import com.perimeterx.internals.cookie.AbstractPXCookie;
import com.perimeterx.internals.cookie.RawCookieData;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.exceptions.PXCookieDecryptionException;
import com.perimeterx.models.exceptions.PXException;
import com.perimeterx.utils.PXLogger;
import org.apache.commons.lang3.StringUtils;


public class PXCookieOriginalTokenValidator implements PXVerifier{

    private static final PXLogger logger = PXLogger.getLogger(PXCookieOriginalTokenValidator.class);

    private PXConfiguration pxConfiguration;

    public PXCookieOriginalTokenValidator (PXConfiguration pxConfiguration){
        this.pxConfiguration = pxConfiguration;
    }

    /**
     * Verify original cookieOrig and set vid, uuid, score on context
     *
     * @param context - request context, data from cookieOrig will be populated
     */
    public boolean verify(PXContext context) {
        try {

            AbstractPXCookie originalCookie  = CookieSelector.selectOriginalTokens(context, pxConfiguration);
            if (!StringUtils.isEmpty(context.getOriginalTokenError()) || originalCookie == null){
                return false;
            }
            String decodedOriginalCookie = originalCookie.getDecodedCookie().toString();
            context.setDecodedOriginalToken(decodedOriginalCookie);
            if (context.getVid() == null) {
                context.setVid(originalCookie.getVID());
            }
            context.setOriginalUuid(originalCookie.getUUID());

            if (!originalCookie.isSecured()) {
                logger.debug("Original token HMAC validation failed, value: " + decodedOriginalCookie  + " user-agent: " + context.getUserAgent());
                context.setOriginalTokenError("validation_failed");
                return false;
            }
        } catch (PXException | PXCookieDecryptionException e) {
            logger.debug("Received an error while decrypting perimeterx original token:" + e.getMessage());
            context.setOriginalTokenError("decryption_failed");
            return false;
        }
        return true;
    }

    private boolean isErrorMobileHeader(String authHeader) {
        return StringUtils.isNumeric(authHeader) && authHeader.length() == 1;
    }

    public String getMobileError(PXContext context) {
        String mobileError = "";
        RawCookieData tokensCookie = context.getTokensCookie();
        RawCookieData authCookie = context.getAuthCookie();
        if (tokensCookie != null && isErrorMobileHeader(tokensCookie.getSelectedCookie())){
            mobileError = tokensCookie.getSelectedCookie();
        }
        else if (authCookie != null && isErrorMobileHeader(authCookie.getSelectedCookie())){
            mobileError = authCookie.getSelectedCookie();
        }
        return mobileError;
    }
}
