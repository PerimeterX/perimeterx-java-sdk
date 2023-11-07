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
 * Created by nitzangoldfeder on 13/04/2017.
 */
@Test
public class CookieV3Test {
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
    public void testCookieV3FailOnNoCookie() {
        this.context = new PXContext(request, ipProvider, hostnameProvider, pxConfiguration);
        PXCookieValidator pxCookieValidator = new PXCookieValidator(pxConfiguration);
        pxCookieValidator.verify(context);
        assertEquals(S2SCallReason.NO_COOKIE.getValue(), context.getS2sCallReason());
    }

    @Test
    public void testCookieV3Pass() {
        String pxCookie = "_px3=2019a92cbfd71f4711e69c0365cc901e4344f842972582b1a52ff8548a2cf05d:m9ON6t71FV8=:1000:X413RgesdhajGQcg3sKOXmDe9OtDLcuus32yUgeXSRCC2Lwx0ecg0fEw7jlS8DzVrfZREhObOmM/l54wTak7uwD0iU9zDqmrmptG+185YKWcbZrpzQAlxoKLcXHOqGuVGjHoa5ScRBUfhpfPH7KelLY5dIvLp6pRz2JeSM/roRQMv0fRlp2xYZMj0UKUFOH1";
        ((MockHttpServletRequest) request).addHeader("cookie", pxCookie);
        ((MockHttpServletRequest) request).addHeader("user-agent", "test_user_agent");
        this.context = new PXContext(request, ipProvider, hostnameProvider, pxConfiguration);
        PXCookieValidator pxCookieValidator = new PXCookieValidator(pxConfiguration);
        boolean verify = pxCookieValidator.verify(context);
        assertTrue(verify);
    }

    @Test
    public void testCookieV3FailOnSensitiveRoute() {
        PXConfiguration configuration = PXConfiguration.builder()
                .cookieKey("COOKIE_KEY_STRING")
                .appId("APP_ID")
                .authToken("AUTH_TOKEN")
                .sensitiveRoutes(new HashSet(Arrays.asList("/login")))
                .build();
        String pxCookie = "_px3=74c096e83d72f304bcae6d91b8017bb1e4a7c270f876ebc08977653c1b724714:LE+3eusyK6vE1d1pvI4t8HDnGQ0NCyr6aPLOIXXwT5Kr9WW1Ficr9WohnPZLdtZn/dHOsEz0fbk0YRYiKP+81g==:1000:GCTf15dR7qk+h8B+G7n3iI+1JCxiUajyAn+OJ4IRnaqMFE69CJ72+vG2m0qqQQhSF+Q13r1oVb0dgFqg0smfyA==";
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
    public void testCookieV3FailOnDecryption() {
        String pxCookie = "_px3=5192aabc3a9134f771ed8e464817a419f3df6fb6c0aaa69f998cbb1a2224f4d3:R1dKoNUcq1e4W+eoC8dYg23pCtVo2wXrOYbybHmYC9FCyo7aEMt+txk1QJqgltOCjcL54g8tkpa8wrlIMLt12w==:1000:v515J1I1muBk4vN1M5IIpA0LhTTpj5ObGk6s/PzOIaQb03Mvq/LewcPsy85aZKsyDHDM/2BPzut7/9hhQCIkiQ==";
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
    public void testCookieV3FailOnFakeCookie() {
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
    public void testCookieV3FailOnCookieExpired() {
        String pxCookie = "_px3=634aa77f6c2c24f80af864b8e45f6678ae3f8b2f105b4bd426cf99f971134513:wcyrtwkdJ5sXYc79xt/DJrtYhc3PGdSMOoYHHd/cK9R9S3DJf8BKkL+U/gUDWpSRBY+MVALebg8u4sY8sgfcfQ==:1000:Xnn+L6scXhrw7UBBkfLEhkHJ15BspyH3HyspJnoC0Lx4eA67169cbbmzSYJQfbAor1SgS8+Ae1KQXPdaI4+xew==";
        ((MockHttpServletRequest) request).addHeader("cookie", pxCookie);
        ((MockHttpServletRequest) request).addHeader("user-agent", "test_user_agent");
        this.context = new PXContext(request, ipProvider, hostnameProvider, pxConfiguration);
        PXCookieValidator pxCookieValidator = new PXCookieValidator(pxConfiguration);
        boolean verify = pxCookieValidator.verify(context);
        assertFalse(verify);
        assertEquals(S2SCallReason.COOKIE_EXPIRED.getValue(), context.getS2sCallReason());
    }

    @Test
    public void testCookieV3PassAndHighScore() {
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
    public void testCookieV3PassLowScore() {
        String pxCookie = "_px3=74c096e83d72f304bcae6d91b8017bb1e4a7c270f876ebc08977653c1b724714:LE+3eusyK6vE1d1pvI4t8HDnGQ0NCyr6aPLOIXXwT5Kr9WW1Ficr9WohnPZLdtZn/dHOsEz0fbk0YRYiKP+81g==:1000:GCTf15dR7qk+h8B+G7n3iI+1JCxiUajyAn+OJ4IRnaqMFE69CJ72+vG2m0qqQQhSF+Q13r1oVb0dgFqg0smfyA==";
        ((MockHttpServletRequest) request).addHeader("cookie", pxCookie);
        ((MockHttpServletRequest) request).addHeader("user-agent", "test_user_agent");
        this.context = new PXContext(request, ipProvider, hostnameProvider, pxConfiguration);
        PXCookieValidator pxCookieValidator = new PXCookieValidator(pxConfiguration);
        boolean verify = pxCookieValidator.verify(context);
        assertTrue(verify);
        assertEquals(context.getBlockReason(), BlockReason.NONE);
        assertEquals(S2SCallReason.NONE.getValue(), context.getS2sCallReason());
    }

}
