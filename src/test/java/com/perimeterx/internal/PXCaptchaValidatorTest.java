package com.perimeterx.internal;

import com.perimeterx.api.PXConfiguration;
import com.perimeterx.api.providers.DefaultHostnameProvider;
import com.perimeterx.api.providers.HostnameProvider;
import com.perimeterx.api.providers.IPProvider;
import com.perimeterx.api.providers.RemoteAddressIPProvider;
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
    private IPProvider ipProvider;
    private HostnameProvider hostnameProvider;
    private PXConfiguration pxConfig;
    @BeforeClass
    public void setUp() throws Exception {
        pxConfig = new PXConfiguration.Builder()
                .appId("APP_ID")
                .authToken("AUTH_123")
                .cookieKey("COOKIE_123")
                .build();
        PXClient client = TestObjectUtils.verifiedCaptchaClient();
        PXClient noVerificationClient = TestObjectUtils.notVerifiedCaptchaClient();
        this.captchaValidator = new PXCaptchaValidator(client);
        this.noValidateCaptchaValidator = new PXCaptchaValidator(noVerificationClient);
        this.ipProvider = new RemoteAddressIPProvider();
        this.hostnameProvider = new DefaultHostnameProvider();
    }

    public void verifyNoCaptchaCookie_noCookie() throws PXException {
        MockHttpServletRequest noCaptchaCookieReq = new MockHttpServletRequest();
        PXContext context = new PXContext(noCaptchaCookieReq, this.ipProvider, this.hostnameProvider, pxConfig);
        boolean verify = this.captchaValidator.verify(context);
        Assert.assertEquals(verify, false);
    }

    public void verifyNoCaptchaCookie_verified() throws PXException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("cookie", "_pxCaptcha=test:vid:uuid");
        PXContext context = new PXContext(request, this.ipProvider, this.hostnameProvider, pxConfig);
        boolean verify = this.captchaValidator.verify(context);
        Assert.assertEquals(verify, true);
    }

    public void verifyNoCaptchaCookie_notVerified() throws PXException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("cookie", "_pxCaptcha=test:vid:uuid");
        PXContext context = new PXContext(request, this.ipProvider, this.hostnameProvider, pxConfig);
        boolean verify = this.noValidateCaptchaValidator.verify(context);
        Assert.assertEquals(verify, false);
    }
}
