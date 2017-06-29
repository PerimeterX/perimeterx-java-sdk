package com.perimeterx.internals;

import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.api.PerimeterX;
import com.perimeterx.internals.cookie.PXCookie;
import com.perimeterx.internals.cookie.PXCookieFactory;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.exceptions.PXCookieDecryptionException;
import com.perimeterx.models.exceptions.PXException;
import com.perimeterx.models.risk.BlockReason;
import com.perimeterx.models.risk.PassReason;
import com.perimeterx.models.risk.S2SCallReason;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PXCookieValidator
 * <p>
 * Created by shikloshi on 07/07/2016.
 */
public class PXCookieValidator {

    private Logger L = LoggerFactory.getLogger(PerimeterX.class);

    public static PXCookieValidator getDecoder(String cookieKey) throws PXException {
        try {
            PXCookieValidator cookieValidator = new PXCookieValidator();
            return cookieValidator;
        } catch (Exception e) {
            throw new PXException(e);
        }
    }

    /**
     * Verify cookie and set vid, uuid, score on context
     *
     * @param context - request context, data from cookie will be populated
     * @return S2S call reason according to the result of cookie verification
     */
    public boolean verify(PXConfiguration pxConfiguration, PXContext context) {
        try {
            PXCookie pxCookie = PXCookieFactory.create( pxConfiguration,context);
            if (pxCookie == null ) {
                 context.setS2sCallReason(S2SCallReason.NO_COOKIE);
                 return false;
            }

            // In case pxCookie will be modified from the outside extracting the cookie on the constructor
            // will fail, we test for null for the cookie before, if its null then we want to set pxCookieOrig
            if (pxCookie.getPxCookie() == null || !pxCookie.deserialize()){
                context.setPxCookieOrig(context.getPxCookie());
                context.setS2sCallReason(S2SCallReason.INVALID_DECRYPTION);
                return false;
            }

            context.setRiskCookie(pxCookie);
            context.setVid(pxCookie.getVID());
            context.setUuid(pxCookie.getUUID());
            context.setScore(pxCookie.getScore());
            context.setBlockAction(pxCookie.getBlockAction());
            context.setCookieHmac(pxCookie.getHmac());

            if (pxCookie.isExpired()){
                context.setS2sCallReason(S2SCallReason.COOKIE_EXPIRED);
                return false;
            }

            if (pxCookie.isHighScore()){
                context.setBlockReason(BlockReason.COOKIE);
                return true;
            }

            if (!pxCookie.isSecured()){
                context.setS2sCallReason(S2SCallReason.INVALID_VERIFICATION);
                return false;
            }

            if (context.isSensitiveRoute()){
                context.setS2sCallReason(S2SCallReason.SENSITIVE_ROUTE);
                return false;
            }
            context.setPassReason(PassReason.COOKIE);
            context.setS2sCallReason(S2SCallReason.NONE);
            return true;

        } catch ( PXException | PXCookieDecryptionException e) {
            L.error(e.getMessage());

            context.setPxCookieOrig(context.getPxCookie());
            context.setS2sCallReason(S2SCallReason.INVALID_DECRYPTION);
            return false;
        }
    }
}
