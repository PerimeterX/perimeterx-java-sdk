package com.perimeterx.internals;

import com.perimeterx.internals.cookie.AbstractPXCookie;
import com.perimeterx.internals.cookie.CookieData;
import com.perimeterx.internals.cookie.PXCookieFactory;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.exceptions.PXCookieDecryptionException;
import com.perimeterx.models.exceptions.PXException;
import com.perimeterx.models.risk.BlockReason;
import com.perimeterx.models.risk.PassReason;
import com.perimeterx.models.risk.S2SCallReason;
import com.perimeterx.utils.Constants;
import com.perimeterx.utils.PXLogger;
import org.apache.commons.lang3.StringUtils;

/**
 * PXCookieValidator
 * <p>
 * Created by shikloshi on 07/07/2016.
 */
public class PXCookieValidator implements PXVerifier{

    private static final PXLogger logger = PXLogger.getLogger(PXCookieValidator.class);

    private PXConfiguration pxConfiguration;



    public PXCookieValidator (PXConfiguration pxConfiguration) {
        this.pxConfiguration = pxConfiguration;
    }

    /**
     * Verify cookie and set vid, uuid, score on context
     *
     * @param context - request context, data from cookie will be populated
     * @return S2S call reason according to the result of cookie verification
     */
    public boolean verify( PXContext context) {
        AbstractPXCookie pxCookie = null;

        try {
            boolean isErrorCookie = false;
            String authHeader = context.getHeaders().get(Constants.MOBILE_SDK_HEADER);
            if (context.isMobileToken()) {
                PXCookieOriginalTokenValidator mobileValidator = new PXCookieOriginalTokenValidator(pxConfiguration);
                isErrorCookie = mobileValidator.isErrorMobileHeader(authHeader);
                String originalToken = context.getOriginalToken();
                if(!StringUtils.isEmpty(originalToken)){
                    context.setDeserializeFromOriginalToken(true);
                    mobileValidator.verify(context);
                }
            }
            if (isErrorCookie){
                context.setS2sCallReason("mobile_error_" + authHeader);
                return false;
            }
            CookieData cookieData = CookieData.builder().ip(context.getIp())
                    .mobileToken(context.isMobileToken())
                    .pxCookies(context.getPxCookies())
                    .userAgent(context.getUserAgent())
                    .cookie(context.getRawCookie())
                    .build();

            pxCookie = PXCookieFactory.create(pxConfiguration, cookieData);
            if (pxCookie == null) {
                context.setS2sCallReason(S2SCallReason.NO_COOKIE.name());
                return false;
            }

            // In case pxCookie will be modified from the outside extracting the cookie on the constructor
            // will fail, we test for null for the cookie before, if its null then we want to set pxCookieOrig
            if (pxCookie.getPxCookie() == null || !pxCookie.deserialize()) {
                context.setS2sCallReason(S2SCallReason.INVALID_DECRYPTION.name());
                return false;
            }

            context.setRiskCookie(pxCookie);
            context.setVid(pxCookie.getVID());
            context.setUuid(pxCookie.getUUID());
            context.setRiskScore(pxCookie.getScore());
            context.setBlockAction(pxCookie.getBlockAction());
            context.setCookieHmac(pxCookie.getHmac());

            if (pxCookie.isExpired()) {
                logger.debug(PXLogger.LogReason.DEBUG_COOKIE_TLL_EXPIRED, pxCookie.getPxCookie(), System.currentTimeMillis() - pxCookie.getTimestamp());
                context.setS2sCallReason(S2SCallReason.COOKIE_EXPIRED.name());
                return false;
            }

            if (pxCookie.isHighScore()) {
                context.setBlockReason(BlockReason.COOKIE);
                return true;
            }

            if (!pxCookie.isSecured()) {
                context.setS2sCallReason(S2SCallReason.INVALID_VERIFICATION.name());
                return false;
            }

            if (context.isSensitiveRoute()) {
                logger.debug(PXLogger.LogReason.DEBUG_S2S_RISK_API_SENSITIVE_ROUTE, context.getUri());
                context.setS2sCallReason(S2SCallReason.SENSITIVE_ROUTE.name());
                return false;
            }
            context.setPassReason(PassReason.COOKIE);
            context.setS2sCallReason(S2SCallReason.NONE.name());
            return true;

        } catch (PXException | PXCookieDecryptionException e) {
            logger.error(PXLogger.LogReason.DEBUG_COOKIE_DECRYPTION_FAILED, pxCookie);
            context.setS2sCallReason(S2SCallReason.INVALID_DECRYPTION.name());
            return false;
        }
    }


}
