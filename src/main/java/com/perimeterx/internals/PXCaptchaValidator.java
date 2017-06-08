package com.perimeterx.internals;

import com.perimeterx.http.PXClient;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.exceptions.PXException;
import com.perimeterx.models.httpmodels.CaptchaRequest;
import com.perimeterx.models.httpmodels.CaptchaResponse;
import com.perimeterx.models.risk.BlockReason;
import com.perimeterx.models.risk.PassReason;
import com.perimeterx.utils.Constants;
import org.apache.http.conn.ConnectTimeoutException;

/**
 * PXCaptchaValidator - Validate captcha token from request using PX Server
 * <p>
 * Created by shikloshi on 07/07/2016.
 */
public class PXCaptchaValidator {

    private PXClient pxClient;

    public PXCaptchaValidator(PXClient pxClient) {
        this.pxClient = pxClient;
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
            CaptchaRequest captchaRequest = CaptchaRequest.fromContext(context);
            CaptchaResponse r = this.pxClient.sendCaptchaRequest(captchaRequest);
            context.setRiskRtt(System.currentTimeMillis() - startRiskRtt);
            if (r != null && r.getStatus() == Constants.CAPTCHA_SUCCESS_CODE) {
                context.setVid(r.getVid());
                context.setPassReason(PassReason.CAPTCHA);
                return true;
            }
            context.setBlockReason(BlockReason.SERVER);
            return false;
        } catch (ConnectTimeoutException e) {
            // Timeout handling - report pass reason and proceed with request
            context.setPassReason(PassReason.CAPTCHA_TIMEOUT);
            return true;
        } catch (Exception e) {
            context.setRiskRtt(System.currentTimeMillis() - startRiskRtt);
            throw new PXException(e);
        }
    }

    private boolean captchaCookieIsEmpty(String pxCaptchaCookie) {
        return pxCaptchaCookie == null || "".equals(pxCaptchaCookie);
    }
}
