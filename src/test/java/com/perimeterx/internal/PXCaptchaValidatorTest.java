package com.perimeterx.internal;

import com.perimeterx.http.PXClient;
import com.perimeterx.internals.PXCaptchaValidator;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.exceptions.PXException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import testutils.TestObjectUtils;

/**
 * Created by shikloshi on 16/07/2016.
 */
@Test
public class PXCaptchaValidatorTest {

    private PXCaptchaValidator captchaValidator;
    private PXCaptchaValidator noValidateCaptchaValidator;

    @BeforeClass
    public void setUp() throws Exception {
        PXClient client = TestObjectUtils.verifiedCaptchaClient();
        PXClient noVerificationClient = TestObjectUtils.notVerifiedCaptchaClient();
        this.captchaValidator = new PXCaptchaValidator(client);
        this.noValidateCaptchaValidator = new PXCaptchaValidator(noVerificationClient);
    }

    public void verifyNoCaptchaCookie_noCookie() throws PXException {
        MockHttpServletRequest noCaptchaCookieReq = new MockHttpServletRequest();
        PXContext context = new PXContext(noCaptchaCookieReq);
        boolean verify = this.captchaValidator.verify(context);
        Assert.assertEquals(verify, false);
    }

    public void verifyNoCaptchaCookie_verified() throws PXException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("cookie", "_pxCaptcha=test:vid");
        PXContext context = new PXContext(request);
        boolean verify = this.captchaValidator.verify(context);
        Assert.assertEquals(verify, true);
    }

    public void verifyNoCaptchaCookie_notVerified() throws PXException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("cookie", "_pxCaptcha=test:vid");
        PXContext context = new PXContext(request);
        boolean verify = this.noValidateCaptchaValidator.verify(context);
        Assert.assertEquals(verify, false);
    }
}
