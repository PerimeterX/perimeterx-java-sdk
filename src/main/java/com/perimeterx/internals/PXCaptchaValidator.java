package com.perimeterx.internals;

import com.perimeterx.http.PXClient;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.exceptions.PXException;
import com.perimeterx.models.httpmodels.CaptchaRequest;
import com.perimeterx.models.httpmodels.CaptchaResponse;
import com.perimeterx.utils.Constants;

import java.io.IOException;

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
        try {
            CaptchaRequest captchaRequest = new CaptchaRequest(context);
            CaptchaResponse r = this.pxClient.sendCaptchaRequest(captchaRequest);
            if (r.getStatus() == Constants.CAPTCHA_SUCCESS_CODE) {
                context.setVid(r.getVid());
                return true;
            }
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    private boolean captchaCookieIsEmpty(String pxCaptchaCookie) {
        return pxCaptchaCookie == null || "".equals(pxCaptchaCookie);
    }
}
