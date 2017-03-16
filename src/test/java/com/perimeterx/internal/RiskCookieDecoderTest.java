package com.perimeterx.internal;

import com.perimeterx.api.providers.DefaultHostnameProvider;
import com.perimeterx.api.providers.HostnameProvider;
import com.perimeterx.api.providers.IPProvider;
import com.perimeterx.api.providers.RemoteAddressIPProvider;
import com.perimeterx.http.PXClient;
import com.perimeterx.internals.PXCookieValidator;
import com.perimeterx.internals.PXS2SValidator;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.exceptions.PXException;
import com.perimeterx.models.httpmodels.RiskRequest;
import com.perimeterx.models.risk.S2SCallReason;
import com.perimeterx.utils.Constants;
import static org.junit.Assert.*;
import org.springframework.mock.web.MockHttpServletRequest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import testutils.PXClientMock;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by shikloshi on 22/11/2016.
 */

@Test
public class RiskCookieDecoderTest {
    private HttpServletRequest request;

    private PXContext context;
    private IPProvider ipProvider;
    private HostnameProvider hostnameProvider;
    private PXCookieValidator cookieValidator;

    @BeforeTest
    public void setUp() throws PXException {
        this.request = new MockHttpServletRequest();
        ((MockHttpServletRequest) request).addHeader("cookie","_px=BlehldpSBV8F5ZPRa4MTbYQKbkIaXLO2AETbUSiZUobT1mvohRff7C+5EoTuKr+F1pkntRC5FGla2PWGYZiJ8g==:1000:3AWMJr6ZBQ0S6d74mSsQQJR9cWZn7LJGaSLqhX3k430YeySP71Onzxla+Qvw0PiSp9N8nIyPvYychFiG0n6ebl7eBWyWb9eTQraBzFRjWjWyuZDMr1C5e1QZ8TrMy+bUeaIuvm3J3k9pmawdHdpkyyL2HSyr35vKX5kAA1I/eWFrAETsz8UZ3xZM8cqwnq1UDQpLLWNu4ZXpDi0OLbLr+lD+mpe82Xir6TGaLVegrBZJNzJzIOhNDfpD/GXYDYpNjOtngDHWs7Se+yErPwNfmg==");
//        ((MockHttpServletRequest) request).setCookies(new Cookie("_px","BlehldpSBV8F5ZPRa4MTbYQKbkIaXLO2AETbUSiZUobT1mvohRff7C+5EoTuKr+F1pkntRC5FGla2PWGYZiJ8g==:1000:3AWMJr6ZBQ0S6d74mSsQQJR9cWZn7LJGaSLqhX3k430YeySP71Onzxla+Qvw0PiSp9N8nIyPvYychFiG0n6ebl7eBWyWb9eTQraBzFRjWjWyuZDMr1C5e1QZ8TrMy+bUeaIuvm3J3k9pmawdHdpkyyL2HSyr35vKX5kAA1I/eWFrAETsz8UZ3xZM8cqwnq1UDQpLLWNu4ZXpDi0OLbLr+lD+mpe82Xir6TGaLVegrBZJNzJzIOhNDfpD/GXYDYpNjOtngDHWs7Se+yErPwNfmg=="));

        this.ipProvider = new RemoteAddressIPProvider();
        this.hostnameProvider = new DefaultHostnameProvider();
        this.context = new PXContext(request, this.ipProvider, this.hostnameProvider, "appId");
        this.cookieValidator = PXCookieValidator.getDecoder("cookie_token");
    }

    @Test
    public void testOrigPXCookieSentOnCookieDecryptFails(){
        S2SCallReason verify = cookieValidator.verify(context);
        assertEquals(verify, S2SCallReason.INVALID_DECRYPTION);
        assertNotNull(context.getPxCookieOrig());
    }
}
