package com.perimeterx.internals;

import com.perimeterx.api.PerimeterX;
import com.perimeterx.internals.cookie.AbstractPXCookie;
import com.perimeterx.internals.cookie.RawCookieData;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.exceptions.PXCookieDecryptionException;
import com.perimeterx.models.exceptions.PXException;
import com.perimeterx.utils.logger.IPXLogger;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class PXCookieOriginalTokenValidator implements PXValidator {

    private PXConfiguration pxConfiguration;

    public PXCookieOriginalTokenValidator(PXConfiguration pxConfiguration) {
        this.pxConfiguration = pxConfiguration;
    }

    /**
     * Verify original cookieOrig and set vid, uuid, score on context
     *
     * @param context - request context, data from cookieOrig will be populated
     */
    public boolean verify(PXContext context) {
        try {

            AbstractPXCookie originalCookie = CookieSelector.selectOriginalTokens(context, pxConfiguration);
            if (!StringUtils.isEmpty(context.getOriginalTokenError()) || originalCookie == null) {
                return false;
            }
            String decodedOriginalCookie = originalCookie.getDecodedCookie().toString();
            context.setOriginalTokenCookie(originalCookie.getCookieOrig());
            context.setDecodedOriginalToken(decodedOriginalCookie);
            if (context.getVid() == null) {
                context.setVid(originalCookie.getVID());
            }
            context.setPxCookieRaw(originalCookie.getCookieOrig());
            context.setCookieVersion(originalCookie.getCookieVersion());
            context.setOriginalUuid(originalCookie.getUUID());

            if (!originalCookie.isSecured()) {
                context.logger.debug("Original token HMAC validation failed, value: " + decodedOriginalCookie + " user-agent: " + context.getUserAgent());
                context.setOriginalTokenError("validation_failed");
                return false;
            }
        } catch (PXException | PXCookieDecryptionException e) {
            context.logger.debug("Received an error while decrypting perimeterx original token:" + e.getMessage());
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
        List<RawCookieData> tokensCookie = context.getTokens();
        if (!tokensCookie.isEmpty()) {
            RawCookieData firstCookie = tokensCookie.get(0);
            if (isErrorMobileHeader(firstCookie.getSelectedCookie())) {
                mobileError = firstCookie.getSelectedCookie();
            }
        }
        return mobileError;
    }
}
