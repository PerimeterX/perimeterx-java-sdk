package com.perimeterx.internals;

import com.perimeterx.api.PerimeterX;
import com.perimeterx.internals.cookie.RiskCookie;
import com.perimeterx.internals.cookie.RiskCookieDecoder;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.exceptions.PXException;
import com.perimeterx.models.risk.S2SCallReason;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * PXCookieValidator
 * <p>
 * Created by shikloshi on 07/07/2016.
 */
public class PXCookieValidator {

    private RiskCookieDecoder cookieDecoder;
    private Logger L = LoggerFactory.getLogger(PerimeterX.class);

    private void init(String cookieKey) throws Exception {
        this.cookieDecoder = new RiskCookieDecoder(cookieKey);
    }

    public static PXCookieValidator getDecoder(String cookieKey) throws PXException {
        try {
            PXCookieValidator cookieValidator = new PXCookieValidator();
            cookieValidator.init(cookieKey);
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
    public S2SCallReason verify(PXContext context) {
        String pxCookie = context.getPxCookie();
        if (pxCookie == null || pxCookie.isEmpty()) {
            return S2SCallReason.NO_COOKIE;
        }
        S2SCallReason s2sCallReason;
        try {
            RiskCookie riskCookie = cookieDecoder.decryptRiskCookie(pxCookie);
            context.setRiskCookie(riskCookie);
            context.setVid(riskCookie.vid);
            context.setUuid(riskCookie.uuid);
            context.setScore(riskCookie.score.bot);
            RiskCookieDecoder.ValidationResult validate = cookieDecoder.validate(riskCookie, new String[]{context.getUserAgent()});
            switch (validate) {
                case NO_SIGNING:
                case INVALID:
                    s2sCallReason = S2SCallReason.INVALID_VERIFICATION;
                    break;
                case EXPIRED:
                    s2sCallReason = S2SCallReason.EXPIRED_COOKIE;
                    break;
                case VALID:
                    s2sCallReason = S2SCallReason.NONE;
                    break;
                default:
                    s2sCallReason = S2SCallReason.NONE;
            }
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException | NoSuchPaddingException |
                InvalidKeyException | InvalidKeySpecException | NoSuchAlgorithmException | IOException e) {
            L.error(e.getMessage());

            context.setPxCookieOrig(pxCookie);
            s2sCallReason = S2SCallReason.INVALID_DECRYPTION;
        }
        return s2sCallReason;
    }
}
