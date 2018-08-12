package com.perimeterx.internal;

import com.perimeterx.api.providers.DefaultHostnameProvider;
import com.perimeterx.api.providers.HostnameProvider;
import com.perimeterx.api.providers.IPProvider;
import com.perimeterx.api.providers.RemoteAddressIPProvider;
import com.perimeterx.internals.PXCookieValidator;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.exceptions.PXException;
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
 * Created by saar shtalryd on 11/01/2018.
 */
@Test
public class CookieV1MobileTest {
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
        this.pxConfiguration = new PXConfiguration.Builder()
                .cookieKey("COOKIE_KEY_STRING_MOBILE")
                .appId("APP_ID")
                .authToken("AUTH_TOKEN")
                .build();
        this.cookieValidator = new PXCookieValidator(this.pxConfiguration);
    }



    @Test
    public void testCookieV1MobileToken() {
        String pxCookie = "1:eGPYisZ+3qU=:1000:645M2JJ5rYzdSh0T3S6fgtBcHATlIx+A021RUKQpMdr/csetstQJ0/LmAv7HU+gi2Jzd9L47sGGnaCzzcBxbgVd3bwE1dOpKraxwW7iOJ9MGVtlndG8TY5Yvx5mOJFF6Z7Kif1XoicYwNFdPht+KuOoQY8LO51TR4r7b2+OycfvlIKQSVgh41p4SARDbKbFtUM+3VLCoLrYKJT+qq/8a993aZnXlfSc9kqKj89EqS9mz4b7CRhoeVGpMfvDxTFcMicV3AEPlQCdFGUkZonr+yQ==";
        ((MockHttpServletRequest) request).addHeader("x-px-authorization", pxCookie);
        this.context = new PXContext(request, ipProvider, hostnameProvider, pxConfiguration);
        assertEquals(true, this.context.isMobileToken());
    }


    @Test
    public void testCookieV1MobilePass() {
        String pxCookie = "1:eGPYisZ+3qU=:1000:645M2JJ5rYzdSh0T3S6fgtBcHATlIx+A021RUKQpMdr/csetstQJ0/LmAv7HU+gi2Jzd9L47sGGnaCzzcBxbgVd3bwE1dOpKraxwW7iOJ9MGVtlndG8TY5Yvx5mOJFF6Z7Kif1XoicYwNFdPht+KuOoQY8LO51TR4r7b2+OycfvlIKQSVgh41p4SARDbKbFtUM+3VLCoLrYKJT+qq/8a993aZnXlfSc9kqKj89EqS9mz4b7CRhoeVGpMfvDxTFcMicV3AEPlQCdFGUkZonr+yQ==";
        ((MockHttpServletRequest) request).addHeader("x-px-authorization", pxCookie);
        this.context = new PXContext(request, ipProvider, hostnameProvider, pxConfiguration);
        boolean verify = cookieValidator.verify(context);
        assertEquals(true, verify);
    }

    @Test
    public void testCookieV1MobileFailOnSensitiveRoute() {
        PXConfiguration configuration = new PXConfiguration.Builder()
                .cookieKey("COOKIE_KEY_STRING_MOBILE")
                .appId("APP_ID")
                .authToken("AUTH_TOKEN")
                .sensitiveRoutes(new HashSet(Arrays.asList("/login")))
                .build();
        String pxCookie = "1:eGPYisZ+3qU=:1000:645M2JJ5rYzdSh0T3S6fgtBcHATlIx+A021RUKQpMdr/csetstQJ0/LmAv7HU+gi2Jzd9L47sGGnaCzzcBxbgVd3bwE1dOpKraxwW7iOJ9MGVtlndG8TY5Yvx5mOJFF6Z7Kif1XoicYwNFdPht+KuOoQY8LO51TR4r7b2+OycfvlIKQSVgh41p4SARDbKbFtUM+3VLCoLrYKJT+qq/8a993aZnXlfSc9kqKj89EqS9mz4b7CRhoeVGpMfvDxTFcMicV3AEPlQCdFGUkZonr+yQ==";
        ((MockHttpServletRequest) request).setRequestURI("/login/user");
        ((MockHttpServletRequest) request).addHeader("x-px-authorization", pxCookie);
        this.context = new PXContext(request, ipProvider, hostnameProvider, configuration);
        assertTrue(this.context.isSensitiveRoute());
        boolean verify = cookieValidator.verify(context);
        assertFalse(verify);
        assertEquals(S2SCallReason.SENSITIVE_ROUTE.name(), context.getS2sCallReason());
    }

    @Test
    public void testCookieV1MobileFailOnDecryption() {
        String pxCookie = "1:eGPYisZ+3qU=:1000:645M2JJ5rYzdSh0T3S6fgtBcHATlIx+A021RUKQpMdr/csetstQJ0/LmAv7HU+gi2Jzd9L47sGGnaCzzcBxbgVd3bwE1dOpKraxwW7iOJ9MGVtlndG8TY5Yvx5mOJFF6Z7Kif1XoicYwNFdPht+KuOoQY8LO51TR4r7b2+OycfvlIKQSVgh41p4SARDbKbFtUM+3VLCoLrYKJT+qq/8a993aZnXlfSc9kqKj89EqS9mz4b7CRhoeVGpMfvDxTFcMicV3AEPlQCdFGUkZonr+yQ==";
        ((MockHttpServletRequest) request).addHeader("x-px-authorization", pxCookie);
        this.pxConfiguration = new PXConfiguration.Builder()
                .cookieKey("INVALID COOKIE TOKEN")
                .appId("APP_ID")
                .authToken("AUTH_TOKEN")
                .build();
        PXCookieValidator pxCookieValidator = new PXCookieValidator(this.pxConfiguration);
        this.context = new PXContext(request, ipProvider, hostnameProvider, pxConfiguration);
        boolean verify = pxCookieValidator.verify(context);
        assertFalse(verify);
        assertEquals(S2SCallReason.INVALID_DECRYPTION.name(), context.getS2sCallReason());
    }

    @Test
    public void testCookieV1MobileMobileFailOnFakeCookie() {
        String pxCookie = "1:bavs";
        ((MockHttpServletRequest) request).addHeader("x-px-authorization", pxCookie);
        this.context = new PXContext(request, ipProvider, hostnameProvider, pxConfiguration);
        boolean verify = cookieValidator.verify(context);

        assertFalse(verify);
        assertEquals(S2SCallReason.INVALID_DECRYPTION.name(), context.getS2sCallReason());
    }

    @Test
    public void testCookieV1MobileFailOnCookieExpired() {
        String pxCookie = "1:JpFP+MkTy7Y=:1000:Fp0bg1yaTaJ+X1ztkFNF+J7WpDsfCl594WwtQixoRitLQ5PsqIPcjBiIpSoOgGZ+dpvPUJEjy+NbBM9JFrPyVUGIdZ394YKp/yo+UH6gvf42XAlI5Ci/53YB2UGyD9s5UkAYeJ462dw0eKMDY3I5hOaF6l9LVP5wivx/30yngJ1vqNH74EkqnpJF7kcqbtmcTldhvZpaSOeNGVNH8UJNeCIAZJjQPYJTAOKirICGXUPsxp38ZwFNBE+sMvVBHd+QpXz9zRcqGi3/Pk3/ctGyIg==";
        ((MockHttpServletRequest) request).addHeader("x-px-authorization", pxCookie);
        this.context = new PXContext(request, ipProvider, hostnameProvider, pxConfiguration);
        boolean verify = cookieValidator.verify(context);

        assertFalse(verify);
        assertEquals(S2SCallReason.COOKIE_EXPIRED.name(), context.getS2sCallReason());
    }

    @Test
    public void testCookieV1MobilePassAndHighScore() {
        String pxCookie = "1:qfYWqbWVWzw=:1000:WKxzNJtR1/dUWseaCQlheLlWLZzH1WTY/hGM52jQZf9qrIKXW5yhcc91QuZhipokMM3XJlj3FRQpK+9XVSsYyLOKY0P7k6HVW6auMp/2dub7/cFQqz+o3yLW7b5zRL7tuilGqMPD+EQfDcLOXusJWjdRqN4iV5AK4jSbW7iet9WLiG78t9JBNGCmVvHTgtcwM9hm7MfCBs99LWi0UiZVOZHQ8sasf/+vSydezOPS/SVpKxBIe8TFOf/ZY64ASEg46nVd1Cy62Vqfy+S0KFzXjpnv1cbVIwkruwRixldXobQ=";
        ((MockHttpServletRequest) request).addHeader("x-px-authorization", pxCookie);
        this.context = new PXContext(request, ipProvider, hostnameProvider, pxConfiguration);
        boolean verify = cookieValidator.verify(context);

        assertTrue(verify);
        assertEquals(BlockReason.COOKIE, context.getBlockReason());
    }

    @Test
    public void testCookieV1MobilePassLowScore() {
        String pxCookie = "1:fdQwDG0g0Kk=:1000:Y0mJ7iyek/dManEwojI0uhPBO+8+jA0ntJ4VfKpvjXV65TPL30KS//hs237UWKxxFKZBQdClZcXRDmpgK29HDG7XyVHOUk5/s2ojQwg4pGU17UaXsJxE8TGv5o0b4SsrwoBgT9MKT1feE2bPlWqB+j9UIu/4LRh0OIakiUDAZy32qCAFlS2ZpoEEQLOpSi7Q7VSVf1r9fodtlmMxdF//7eUjkBkHr79kzEJF1Z5VD8zLd3tDv0Lwlv97xVMXlYlo0/dDJOlg8FZ837aKxC+BgQ==";
        ((MockHttpServletRequest) request).addHeader("x-px-authorization", pxCookie);
        this.context = new PXContext(request, ipProvider, hostnameProvider, pxConfiguration);
        boolean verify = cookieValidator.verify(context);

        assertTrue(verify);
        assertEquals(context.getBlockReason(), BlockReason.NONE);
        assertEquals(context.getS2sCallReason(), S2SCallReason.NONE.name());
    }
}
