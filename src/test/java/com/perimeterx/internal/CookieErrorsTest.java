package com.perimeterx.internal;

import com.perimeterx.api.providers.DefaultHostnameProvider;
import com.perimeterx.api.providers.HostnameProvider;
import com.perimeterx.api.providers.IPProvider;
import com.perimeterx.api.providers.RemoteAddressIPProvider;
import com.perimeterx.internals.PXCookieValidator;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.exceptions.PXException;
import com.perimeterx.models.risk.S2SCallReason;
import org.springframework.mock.web.MockHttpServletRequest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertEquals;

/**
 * Created by saar shtalryd on 11/01/2018.
 */
@Test
public class CookieErrorsTest {
    private PXConfiguration pxConfiguration;
    private PXContext context;
    private PXCookieValidator cookieValidator;
    private HttpServletRequest request;
    private IPProvider ipProvider;
    private HostnameProvider hostnameProvider;

    @BeforeMethod
    public void setUp() throws PXException {
        request = new MockHttpServletRequest();
        ipProvider = new RemoteAddressIPProvider();
        hostnameProvider = new DefaultHostnameProvider();
        this.cookieValidator = PXCookieValidator.getDecoder("cookie_token");
        this.pxConfiguration = new PXConfiguration.Builder()
                .cookieKey("COOKIE_KEY_STRING_MOBILE")
                .appId("APP_ID")
                .authToken("AUTH_TOKEN")
                .build();
    }



    @Test
    public void testMobileError1() {
        String pxCookie = "1";
        ((MockHttpServletRequest) request).addHeader("x-px-authorization", pxCookie);
        this.context = new PXContext(request, ipProvider, hostnameProvider, pxConfiguration);
        boolean verify = cookieValidator.verify(pxConfiguration, context);

        assertEquals(false, verify);
        assertEquals(S2SCallReason.NO_COOKIE, context.getS2sCallReason());
    }

    @Test
    public void testMobileError2() {
        String pxCookie = "2";
        ((MockHttpServletRequest) request).addHeader("x-px-authorization", pxCookie);
        this.context = new PXContext(request, ipProvider, hostnameProvider, pxConfiguration);
        boolean verify = cookieValidator.verify(pxConfiguration, context);

        assertEquals(false, verify );
        assertEquals(S2SCallReason.MOBILE_SDK_CONNECTION, context.getS2sCallReason());
    }

    @Test
    public void testMobileError3() {
        String pxCookie = "3";
        ((MockHttpServletRequest) request).addHeader("x-px-authorization", pxCookie);
        this.context = new PXContext(request, ipProvider, hostnameProvider, pxConfiguration);
        boolean verify = cookieValidator.verify(pxConfiguration, context);

        assertEquals(false, verify);
        assertEquals(S2SCallReason.MOBILE_SDK_PINNING, context.getS2sCallReason());
    }
}
