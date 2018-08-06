package com.perimeterx.internals;

import com.perimeterx.internals.cookie.AbstractPXCookie;
import com.perimeterx.internals.cookie.PXCookieFactory;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.exceptions.PXCookieDecryptionException;
import com.perimeterx.models.exceptions.PXException;
import com.perimeterx.utils.PXLogger;


public class PXCookieOriginalTokenValidator {

    private static final PXLogger logger = PXLogger.getLogger(PXCookieOriginalTokenValidator.class);

    /**
     * Verify original cookie and set vid, uuid, score on context
     *
     * @param context - request context, data from cookie will be populated
     */
    public void verify(PXConfiguration pxConfiguration, PXContext context) {
        try {
            AbstractPXCookie originalCookie = PXCookieFactory.create(pxConfiguration, context);
            logger.debug("Original token found, Evaluating");

            if (originalCookie == null ) {
                logger.debug("Original token is null");
                context.setOriginalTokenError("original_token_missing");
                return;
            }

            if (!originalCookie.deserialize()) {
                logger.debug("Original token decryption failed, value: " + context.getOriginalToken());
                context.setOriginalTokenError("decryption_failed");
                return;
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
            }
        } catch (PXException | PXCookieDecryptionException e) {
            logger.debug("Received an error while decrypting perimeterx original token:" + e.getMessage());
            context.setOriginalTokenError("decryption_failed");
        }

    }
}
