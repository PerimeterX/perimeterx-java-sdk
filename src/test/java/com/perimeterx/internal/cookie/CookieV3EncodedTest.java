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
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.*;

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

    @Test
    public void testCookieV3EncodedPass() {
        String pxCookie = "_px3=2019a92cbfd71f4711e69c0365cc901e4344f842972582b1a52ff8548a2cf05d:m9ON6t71FV8=:1000:X413RgesdhajGQcg3sKOXmDe9OtDLcuus32yUgeXSRCC2Lwx0ecg0fEw7jlS8DzVrfZREhObOmM/l54wTak7uwD0iU9zDqmrmptG+185YKWcbZrpzQAlxoKLcXHOqGuVGjHoa5ScRBUfhpfPH7KelLY5dIvLp6pRz2JeSM/roRQMv0fRlp2xYZMj0UKUFOH1";
        ((MockHttpServletRequest) request).addHeader("cookie", pxCookie);
        ((MockHttpServletRequest) request).addHeader("user-agent", "test_user_agent");
        this.context = new PXContext(request, ipProvider, hostnameProvider, pxConfiguration);
        PXCookieValidator pxCookieValidator = new PXCookieValidator(pxConfiguration);
        boolean verify = pxCookieValidator.verify(context);
        assertTrue(verify);
    }

    @Test
    public void testCookieV3EncodedFailOnSensitiveRoute() {
        PXConfiguration configuration = PXConfiguration.builder()
                .cookieKey("COOKIE_KEY_STRING")
                .appId("APP_ID")
                .authToken("AUTH_TOKEN")
                .sensitiveRoutes(new HashSet(Arrays.asList("/login")))
                .build();
        String pxCookie = "_px3=74c096e83d72f304bcae6d91b8017bb1e4a7c270f876ebc08977653c1b724714%3ALE%2B3eusyK6vE1d1pvI4t8HDnGQ0NCyr6aPLOIXXwT5Kr9WW1Ficr9WohnPZLdtZn%2FdHOsEz0fbk0YRYiKP%2B81g%3D%3D%3A1000%3AGCTf15dR7qk%2Bh8B%2BG7n3iI%2B1JCxiUajyAn%2BOJ4IRnaqMFE69CJ72%2BvG2m0qqQQhSF%2BQ13r1oVb0dgFqg0smfyA%3D%3D";
        ((MockHttpServletRequest) request).setRequestURI("/login/user");
        ((MockHttpServletRequest) request).addHeader("cookie", pxCookie);
        ((MockHttpServletRequest) request).addHeader("user-agent", "test_user_agent");
        this.context = new PXContext(request, ipProvider, hostnameProvider, configuration);
        assertTrue(this.context.isSensitiveRoute());
        PXCookieValidator pxCookieValidator = new PXCookieValidator(pxConfiguration);
        boolean verify = pxCookieValidator.verify(context);
        assertFalse(verify);
        assertEquals(S2SCallReason.SENSITIVE_ROUTE.getValue(), context.getS2sCallReason());
    }

    @Test
    public void testCookieV3EncodedFailOnDecryption() {
        String pxCookie = "_px3=192aabc3a9134f771ed8e464817a419f3df6fb6c0aaa69f998cbb1a2224f4d3%3AR1dKoNUcq1e4W%2BeoC8dYg23pCtVo2wXrOYbybHmYC9FCyo7aEMt%2Btxk1QJqgltOCjcL54g8tkpa8wrlIMLt12w%3D%3D%3A1000%3Av515J1I1muBk4vN1M5IIpA0LhTTpj5ObGk6s%2FPzOIaQb03Mvq%2FLewcPsy85aZKsyDHDM%2F2BPzut7%2F9hhQCIkiQ%3D%3D";
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

    @Test
    public void testCookieV3EncodedFailOnCookieExpired() {
        String pxCookie = "_px3=634aa77f6c2c24f80af864b8e45f6678ae3f8b2f105b4bd426cf99f971134513%3AwcyrtwkdJ5sXYc79xt%2FDJrtYhc3PGdSMOoYHHd%2FcK9R9S3DJf8BKkL%2BU%2FgUDWpSRBY%2BMVALebg8u4sY8sgfcfQ%3D%3D%3A1000%3AXnn%2BL6scXhrw7UBBkfLEhkHJ15BspyH3HyspJnoC0Lx4eA67169cbbmzSYJQfbAor1SgS8%2BAe1KQXPdaI4%2Bxew%3D%3D";
        ((MockHttpServletRequest) request).addHeader("cookie", pxCookie);
        ((MockHttpServletRequest) request).addHeader("user-agent", "test_user_agent");
        this.context = new PXContext(request, ipProvider, hostnameProvider, pxConfiguration);
        PXCookieValidator pxCookieValidator = new PXCookieValidator(pxConfiguration);
        boolean verify = pxCookieValidator.verify(context);

        assertFalse(verify);
        assertEquals(S2SCallReason.COOKIE_EXPIRED.getValue(), context.getS2sCallReason());
    }

    @Test
    public void testCookieV3EncodedPassAndHighScore() {
        String pxCookie = "_px3=69777b776fd822edd7857834ca03b09fa5453c260ba603d7b35e2b840480b47b:jE6jQAndx80=:1000:8Feb3FhgDelIXTRjHL2gyOAy+PCyDtKJ3bqhhAVfo8Sjdw2swLosAd6vSqXH/PCI4DAJezgZSf6AVAYbzU+JW/9v6gy9+uxjpvkYPY3oLvTeJp+f3FaXzUV9qYE4HZWTzCg1EoVK9D8TKw1g7Rk1C38kzt2X8DMyvSRLimr349Vw7xg3y6Vf2IspMVVy9c7f";
        ((MockHttpServletRequest) request).addHeader("cookie", pxCookie);
        ((MockHttpServletRequest) request).addHeader("user-agent", "test_user_agent");
        this.context = new PXContext(request, ipProvider, hostnameProvider, pxConfiguration);
        PXCookieValidator pxCookieValidator = new PXCookieValidator(pxConfiguration);
        boolean verify = pxCookieValidator.verify(context);
        assertTrue(verify);
        assertEquals(BlockReason.COOKIE, context.getBlockReason());
    }

    @Test
    public void testCookieV3EncodedPassLowScore() {
        String pxCookie = "_px3=74c096e83d72f304bcae6d91b8017bb1e4a7c270f876ebc08977653c1b724714%3ALE%2B3eusyK6vE1d1pvI4t8HDnGQ0NCyr6aPLOIXXwT5Kr9WW1Ficr9WohnPZLdtZn%2FdHOsEz0fbk0YRYiKP%2B81g%3D%3D%3A1000%3AGCTf15dR7qk%2Bh8B%2BG7n3iI%2B1JCxiUajyAn%2BOJ4IRnaqMFE69CJ72%2BvG2m0qqQQhSF%2BQ13r1oVb0dgFqg0smfyA%3D%3D";
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
