package com.perimeterx.internal.cookie;

import com.perimeterx.api.providers.DefaultHostnameProvider;
import com.perimeterx.api.providers.HostnameProvider;
import com.perimeterx.api.providers.IPProvider;
import com.perimeterx.api.providers.RemoteAddressIPProvider;
import com.perimeterx.internals.PXCookieValidator;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.risk.BlockReason;
import com.perimeterx.models.risk.S2SCallReason;
import org.springframework.mock.web.MockHttpServletRequest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import testutils.CookieV3Generator;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.*;
import static testutils.TestObjectUtils.cookieSecretsDataProvider;

/**
 * Created by johnnytordgeman on 05/07/2018.
 */
@Test
public class CookieV3EncodedTest {
    private PXConfiguration pxConfiguration;
    private PXContext context;
    private HttpServletRequest request;
    private IPProvider ipProvider;
    private HostnameProvider hostnameProvider;

    @BeforeMethod
    @BeforeClass
    public void setUp() {
        request = new MockHttpServletRequest();
        ipProvider = new RemoteAddressIPProvider();
        hostnameProvider = new DefaultHostnameProvider();
        this.pxConfiguration = PXConfiguration.builder()
                .cookieKey("COOKIE_KEY_STRING")
                .appId("APP_ID")
                .authToken("AUTH_TOKEN")
                .build();
    }

    @Test
    public void testCookieV3EncodedFailOnNoCookie() {
        this.context = new PXContext(request, ipProvider, hostnameProvider, pxConfiguration);
        PXCookieValidator pxCookieValidator = new PXCookieValidator(pxConfiguration);
        pxCookieValidator.verify(context);
        assertEquals(S2SCallReason.NO_COOKIE.getValue(), context.getS2sCallReason());
    }

    @DataProvider(name ="cookieSecret")
    public Object[][] cookieSecrets() {
        return cookieSecretsDataProvider(this.pxConfiguration);
    }

    @Test(dataProvider = "cookieSecret")
    public void testCookieV3EncodedPass(String cookieSecret) {
        String pxCookie = CookieV3Generator.builder(cookieSecret).build().toString();
        ((MockHttpServletRequest) request).addHeader("cookie", pxCookie);
        ((MockHttpServletRequest) request).addHeader("user-agent", "test_user_agent");
        this.context = new PXContext(request, ipProvider, hostnameProvider, pxConfiguration);
        PXCookieValidator pxCookieValidator = new PXCookieValidator(pxConfiguration);
        boolean verify = pxCookieValidator.verify(context);
        assertTrue(verify);
    }

    @Test
    public void testCookieV3EncodedFailOnSensitiveRoute() {
        String cookieKey = "COOKIE_KEY_STRING";
        PXConfiguration configuration = PXConfiguration.builder()
                .cookieKey(cookieKey)
                .appId("APP_ID")
                .authToken("AUTH_TOKEN")
                .sensitiveRoutes(new HashSet(Arrays.asList("/login")))
                .build();
        String pxCookie = CookieV3Generator.builder(cookieKey).build().toString();
        ((MockHttpServletRequest) request).setServletPath("/login/user");
        ((MockHttpServletRequest) request).addHeader("cookie", pxCookie);
        ((MockHttpServletRequest) request).addHeader("user-agent", "test_user_agent");
        this.context = new PXContext(request, ipProvider, hostnameProvider, configuration);
        assertTrue(this.context.isSensitiveRequest());
        PXCookieValidator pxCookieValidator = new PXCookieValidator(pxConfiguration);
        boolean verify = pxCookieValidator.verify(context);
        assertFalse(verify);
        assertEquals(S2SCallReason.SENSITIVE_ROUTE.getValue(), context.getS2sCallReason());
    }

    @Test
    public void testCookieV3EncodedFailOnDecryption() {
        String pxCookie = CookieV3Generator.builder("COOKIE_KEY_STRING").build().toString();
        ((MockHttpServletRequest) request).addHeader("cookie", pxCookie);
        this.pxConfiguration = PXConfiguration.builder()
                .cookieKey("INVALID COOKIE TOKEN")
                .appId("APP_ID")
                .authToken("AUTH_TOKEN")
                .build();

        this.context = new PXContext(request, ipProvider, hostnameProvider, pxConfiguration);
        PXCookieValidator pxCookieValidator = new PXCookieValidator(pxConfiguration);
        boolean verify = pxCookieValidator.verify(context);
        assertFalse(verify);
        assertEquals(S2SCallReason.INVALID_DECRYPTION.getValue(), context.getS2sCallReason());
    }

    @Test
    public void testCookieV3EncodedFailOnFakeCookie() {
        String pxCookie = "_px3=bavs";
        ((MockHttpServletRequest) request).addHeader("cookie", pxCookie);
        ((MockHttpServletRequest) request).addHeader("user-agent", "test_user_agent");
        this.context = new PXContext(request, ipProvider, hostnameProvider, pxConfiguration);
        PXCookieValidator pxCookieValidator = new PXCookieValidator(pxConfiguration);
        boolean verify = pxCookieValidator.verify(context);

        assertFalse(verify);
        assertEquals(S2SCallReason.INVALID_DECRYPTION.getValue(), context.getS2sCallReason());
    }

    @Test(dataProvider = "cookieSecret")
    public void testCookieV3EncodedFailOnCookieExpired(String cookieSecret) {
        String pxCookie = CookieV3Generator.builder(cookieSecret)
                .expiryDate(LocalDateTime.now().minusMinutes(5))
                .build()
                .toString();
        ((MockHttpServletRequest) request).addHeader("cookie", pxCookie);
        ((MockHttpServletRequest) request).addHeader("user-agent", "test_user_agent");
        this.context = new PXContext(request, ipProvider, hostnameProvider, pxConfiguration);
        PXCookieValidator pxCookieValidator = new PXCookieValidator(pxConfiguration);
        boolean verify = pxCookieValidator.verify(context);

        assertFalse(verify);
        assertEquals(S2SCallReason.COOKIE_EXPIRED.getValue(), context.getS2sCallReason());
    }

    @Test(dataProvider = "cookieSecret")
    public void testCookieV3EncodedPassAndHighScore(String cookieSecret) {
        String pxCookie = CookieV3Generator.builder(cookieSecret).score(100).build().toString();
        ((MockHttpServletRequest) request).addHeader("cookie", pxCookie);
        ((MockHttpServletRequest) request).addHeader("user-agent", "test_user_agent");
        this.context = new PXContext(request, ipProvider, hostnameProvider, pxConfiguration);
        PXCookieValidator pxCookieValidator = new PXCookieValidator(pxConfiguration);
        boolean verify = pxCookieValidator.verify(context);
        assertTrue(verify);
        assertEquals(BlockReason.COOKIE, context.getBlockReason());
    }

    @Test(dataProvider = "cookieSecret")
    public void testCookieV3EncodedPassLowScore(String cookieSecret) {
        String pxCookie = CookieV3Generator.builder(cookieSecret).build().toString();
        ((MockHttpServletRequest) request).addHeader("cookie", pxCookie);
        ((MockHttpServletRequest) request).addHeader("user-agent", "test_user_agent");
        this.context = new PXContext(request, ipProvider, hostnameProvider, pxConfiguration);
        PXCookieValidator pxCookieValidator = new PXCookieValidator(pxConfiguration);
        boolean verify = pxCookieValidator.verify(context);
        assertTrue(verify);
        assertEquals(context.getBlockReason(), BlockReason.NONE);
        assertEquals(context.getS2sCallReason(), S2SCallReason.NONE.getValue());
    }

}
