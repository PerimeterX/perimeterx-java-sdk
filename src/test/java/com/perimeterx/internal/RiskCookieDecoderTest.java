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
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertEquals;

/**
 * Created by shikloshi on 22/11/2016.
 */

@Test
public class RiskCookieDecoderTest {

    private final String cookie = "BlehldpSBV8F5ZPRa4MTbYQKbkIaXLO2AETbUSiZUobT1mvohRff7C+5EoTuKr+F1pkntRC5FGla2PWGYZiJ8g==:1000:3AWMJr6ZBQ0S6d74mSsQQJR9cWZn7LJGaSLqhX3k430YeySP71Onzxla+Qvw0PiSp9N8nIyPvYychFiG0n6ebl7eBWyWb9eTQraBzFRjWjWyuZDMr1C5e1QZ8TrMy+bUeaIuvm3J3k9pmawdHdpkyyL2HSyr35vKX5kAA1I/eWFrAETsz8UZ3xZM8cqwnq1UDQpLLWNu4ZXpDi0OLbLr+lD+mpe82Xir6TGaLVegrBZJNzJzIOhNDfpD/GXYDYpNjOtngDHWs7Se+yErPwNfmg==";
    private final String pxCookie = "_px=" + "BlehldpSBV8F5ZPRa4MTbYQKbkIaXLO2AETbUSiZUobT1mvohRff7C+5EoTuKr+F1pkntRC5FGla2PWGYZiJ8g==:1000:3AWMJr6ZBQ0S6d74mSsQQJR9cWZn7LJGaSLqhX3k430YeySP71Onzxla+Qvw0PiSp9N8nIyPvYychFiG0n6ebl7eBWyWb9eTQraBzFRjWjWyuZDMr1C5e1QZ8TrMy+bUeaIuvm3J3k9pmawdHdpkyyL2HSyr35vKX5kAA1I/eWFrAETsz8UZ3xZM8cqwnq1UDQpLLWNu4ZXpDi0OLbLr+lD+mpe82Xir6TGaLVegrBZJNzJzIOhNDfpD/GXYDYpNjOtngDHWs7Se+yErPwNfmg==";

    private PXConfiguration pxConfiguration;
    private PXContext context;
    private PXCookieValidator cookieValidator;

    @BeforeTest
    public void setUp() throws PXException {
        HttpServletRequest request = new MockHttpServletRequest();
        ((MockHttpServletRequest) request).addHeader("cookie", pxCookie);
        IPProvider ipProvider = new RemoteAddressIPProvider();
        HostnameProvider hostnameProvider = new DefaultHostnameProvider();
        this.pxConfiguration = new PXConfiguration.Builder()
                .cookieKey("COOKIE_KEY_STRING")
                .appId("APP_ID")
                .authToken("AUTH_TOKEN")
                .build();
        this.cookieValidator = new PXCookieValidator(pxConfiguration);
        this.context = new PXContext(request, ipProvider, hostnameProvider, pxConfiguration);
    }

    @Test
    public void testOrigPXCookieSentOnCookieDecryptFails() {
        cookieValidator.verify(context);
        assertEquals(S2SCallReason.INVALID_DECRYPTION.getValue(), context.getS2sCallReason());
        assertEquals(cookie, context.getPxCookieOrig());
    }
}
