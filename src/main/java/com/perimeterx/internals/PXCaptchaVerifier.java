package com.perimeterx.internals;

import com.perimeterx.http.PXClient;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.exceptions.PXException;
import com.perimeterx.models.httpmodels.CaptchaResponse;
import com.perimeterx.models.httpmodels.ResetCaptchaRequest;
import com.perimeterx.models.risk.BlockReason;
import com.perimeterx.models.risk.PassReason;
import com.perimeterx.utils.Constants;
import com.perimeterx.utils.PXLogger;
import org.apache.http.conn.ConnectTimeoutException;

/**
 * PXCaptchaVerifier - Validate captcha token from request using PX Server
 * <p>
 * Created by shikloshi on 07/07/2016.
 */
public class PXCaptchaVerifier  implements PXVerifier{

    private static final PXLogger logger = PXLogger.getLogger(PXCaptchaVerifier.class);
    private PXClient pxClient;
    private PXConfiguration pxConfiguration;

    public PXCaptchaVerifier(PXClient pxClient, PXConfiguration pxConfiguration) {
        this.pxClient = pxClient;
        this.pxConfiguration = pxConfiguration;
    }

    /**
     * Verify the page request captcha token by querying PX servers
     *
     * @param context - request context
     * @return true if captcha is valid, false if not
     * @throws PXException
     */
    public boolean verify(PXContext context) throws PXException {
        if (captchaCookieIsEmpty(context.getPxCaptcha())) {
            return false;
        }
        long startRiskRtt = System.currentTimeMillis();
        try {
            ResetCaptchaRequest resetCaptchaRequest = ResetCaptchaRequest.fromContext(context, pxConfiguration);
            CaptchaResponse response = this.pxClient.sendCaptchaRequest(resetCaptchaRequest);
            context.setRiskRtt(System.currentTimeMillis() - startRiskRtt);
            if (response != null && response.getStatus() == Constants.CAPTCHA_SUCCESS_CODE) {
                logger.debug(PXLogger.LogReason.DEBUG_CAPTCHA_RESPONSE_SUCCESS);
                context.setVid(response.getVid());
                context.setPassReason(PassReason.CAPTCHA);
                return true;
            }
            context.setBlockReason(BlockReason.SERVER);
            return false;
        } catch (ConnectTimeoutException e) {
            logger.debug(PXLogger.LogReason.DEBUG_CAPTCHA_RESPONSE_TIMEOUT);
            context.setPassReason(PassReason.CAPTCHA_TIMEOUT);
            return true;
        } catch (Exception e) {
            logger.error(PXLogger.LogReason.ERROR_CAPTCHA_RESPONSE_FAILED);
            context.setRiskRtt(System.currentTimeMillis() - startRiskRtt);
            throw new PXException(e);
        }
    }

    private boolean captchaCookieIsEmpty(String pxCaptchaCookie) {
        return pxCaptchaCookie == null || "".equals(pxCaptchaCookie);
    }
}
