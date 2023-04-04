package com.perimeterx.internals;

import com.perimeterx.internals.cookie.AbstractPXCookie;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.exceptions.PXException;
import com.perimeterx.models.risk.BlockReason;
import com.perimeterx.models.risk.PassReason;
import com.perimeterx.models.risk.S2SCallReason;
import com.perimeterx.models.risk.VidSource;
import com.perimeterx.utils.PXLogger;
import org.apache.commons.lang3.StringUtils;

/**
 * PXCookieValidator
 * <p>
 * Created by shikloshi on 07/07/2016.
 */
public class PXCookieValidator implements PXValidator {

    private static final PXLogger logger = PXLogger.getLogger(PXCookieValidator.class);

    private PXConfiguration pxConfiguration;

    public PXCookieValidator(PXConfiguration pxConfiguration) {
        this.pxConfiguration = pxConfiguration;
    }

    /**
     * Verify cookieOrig and set vid, uuid, score on context
     *
     * @param context - request context, data from cookieOrig will be populated
     * @return S2S call reason according to the result of cookieOrig verification
     */
    public boolean verify(PXContext context) {
        AbstractPXCookie pxCookie = null;

        try {
            String mobileError;
            if (context.isMobileToken()) {
                PXCookieOriginalTokenValidator mobileVerifier = new PXCookieOriginalTokenValidator(pxConfiguration);
                mobileError = mobileVerifier.getMobileError(context);
                mobileVerifier.verify(context);
                if (!StringUtils.isEmpty(mobileError)) {
                    context.setS2sCallReason("mobile_error_" + mobileError);
                    return false;
                }
            }
            pxCookie = CookieSelector.selectFromTokens(context, pxConfiguration);
            if (ifLegitPxCookie(context, pxCookie) || pxCookie == null) {
                return false;
            }
            context.setPxCookieRaw(pxCookie.getCookieOrig());
            context.setCookieVersion(pxCookie.getCookieVersion());
            context.setRiskCookie(pxCookie);
            context.setVid(pxCookie.getVID());
            context.setVidSource(VidSource.RISK_COOKIE);
            context.setUuid(pxCookie.getUUID());
            context.setRiskScore(pxCookie.getScore());
            context.setBlockAction(pxCookie.getBlockAction());
            context.setCookieHmac(pxCookie.getHmac());

            if (pxCookie.isExpired()) {
                logger.debug(PXLogger.LogReason.DEBUG_COOKIE_TLL_EXPIRED, pxCookie.getPxCookie(), System.currentTimeMillis() - pxCookie.getTimestamp());
                context.setS2sCallReason(S2SCallReason.COOKIE_EXPIRED.getValue());
                return false;
            }

            if (pxCookie.isHighScore()) {
                context.setBlockReason(BlockReason.COOKIE);
                return true;
            }

            if (!pxCookie.isSecured()) {
                context.setS2sCallReason(S2SCallReason.INVALID_VERIFICATION.getValue());
                return false;
            }

            if (context.isSensitiveRequest()) {
                logger.debug(PXLogger.LogReason.DEBUG_S2S_RISK_API_SENSITIVE_ROUTE, context.getUri());
                context.setS2sCallReason(S2SCallReason.SENSITIVE_ROUTE.getValue());
                return false;
            }
            context.setPassReason(PassReason.COOKIE);
            context.setS2sCallReason(S2SCallReason.NONE.getValue());
            return true;

        } catch (PXException e) {
            logger.error(PXLogger.LogReason.DEBUG_COOKIE_DECRYPTION_HMAC_FAILED, pxCookie);
            context.setS2sCallReason(S2SCallReason.INVALID_VERIFICATION.getValue());
            return false;
        }
    }

    private boolean ifLegitPxCookie(PXContext context, AbstractPXCookie pxCookie) {
        if (StringUtils.isEmpty(context.getS2sCallReason()) && pxCookie == null) {
            context.setS2sCallReason(S2SCallReason.NO_COOKIE.getValue());
        }
        return S2SCallReason.INVALID_DECRYPTION.getValue().equals(context.getS2sCallReason()) || S2SCallReason.NO_COOKIE.getValue().equals(context.getS2sCallReason());
    }
}
