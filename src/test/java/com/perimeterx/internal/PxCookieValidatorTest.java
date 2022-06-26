package com.perimeterx.internal;

import com.perimeterx.api.providers.DefaultHostnameProvider;
import com.perimeterx.api.providers.RemoteAddressIPProvider;
import com.perimeterx.internals.PXCookieValidator;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.exceptions.PXException;
import com.perimeterx.models.risk.S2SCallReason;
import com.perimeterx.utils.Constants;
import org.springframework.mock.web.MockHttpServletRequest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class PxCookieValidatorTest {

    private final String COOKIE_KEY = "ASASDSAd32r223f2+wdsafsar2t43ASCASDFTfTt34gsfet3424tFSDFdsfsdr3R";
    private String CookieV3 = "f2dda03eabfb5056e2258a8910bc4b4ec3caaa5bc246542fe24193c45341112c:ZzY2AgkAS3Dhu6BVQAyn5XxQXQIuYLmnklh0gzPTsif/ItN+kQq46jyq0YPXYahfstf/r4V+mkexvyPl4KKLeA==:1000:ZZht8m6lbnBHMeMtIvQKYSbbea6fiuIQyjxLZhgX9L5ODbE73qBBZ6FJSN5+NWCCzCNWnFAWItIA5e+4l3gi2B6ykh//KiDwqr5jw9XHACwbYs5Jm+seETty+FXmBBL9edlJCKO3lMp54f5lAMagOy4x3oM24wbBeE+BboJrNtUsjUraVLMoVpywOY37BXIT";
    private String CookieV1 = "eGPYisZ+3qU=:1000:7igwrWkws2WEaST6gH9QyFzDWzlLFmR8Y50DG2ZUwCNjdvaZ6niEBqqI68whIuA96G481qX48WSV9GGv0bAhIslbNeZUjpbndMJIAF8O4MqnKTHEVzTGzCCKDfHFIlAlgXFRJTMuR7CIzKzHP9dsqQrE8aRiIvlO9tN5z7em9fvMphIP218Ele+jPnSz0geT4vMFMX8ToCypLsy9UIL58f+97rav/mGCX/hjDBOOfyqY+GgAac2gTHGYLH+9jyWaNviVRH7jIXhnsowdG6ODFQ==";
    private final String PAYLOAD_V3 = "{\"u\":\"484070ee-6783-44c8-86d1-2d9ca4a8eeb8\",\"v\":\"84f7db40-9592-11e8-a7b3-5319fb36a9bf\",\"t\":10416511613331,\"s\":0,\"a\":\"c\",\"sdk_version\":\"v1.5.4\"}";
    private final String PAYLOAD_V1 = "{\"v\":\"b844b860-92fe-11e8-9ad2-075a911258e2\",\"u\":\"e87f6f10-9fb6-11e8-9d8d-572ad86677e3\",\"s\":{\"a\":0,\"b\":0},\"t\":10416511613331,\"h\":\"a011d80fabe2a31d2437858dbb28125e29616be4a327b528334bc9c2ea4fd782\"}";
    private PXConfiguration pxConfiguration;
    private DefaultHostnameProvider hostnameProvider;
    private RemoteAddressIPProvider ipProvider;

    @BeforeClass
    public void init() {
        this.pxConfiguration = PXConfiguration.builder()
                .appId("APP_ID")
                .authToken("AUTH_123")
                .cookieKey(COOKIE_KEY)
                .build();
        this.hostnameProvider = new DefaultHostnameProvider();
        this.ipProvider = new RemoteAddressIPProvider();
    }

    @Test
    public void testVerify() throws PXException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        String cookies = Constants.COOKIE_V3_KEY + ":" + CookieV3 + "," + Constants.COOKIE_V1_KEY + ":" + CookieV1;
        enrichHttpRequestWithPxHeaders(request, Constants.MOBILE_SDK_AUTHORIZATION_HEADER, CookieV1);
        enrichHttpRequestWithPxHeaders(request, Constants.MOBILE_SDK_TOKENS_HEADER, cookies);
        PXCookieValidator pxCookieValidator = new PXCookieValidator(pxConfiguration);
        PXContext pxContext = new PXContext(request, ipProvider, hostnameProvider, pxConfiguration);
        boolean verified = pxCookieValidator.verify(pxContext);
        assertTrue(verified);
        assertEquals(pxContext.getCookieVersion(), Constants.COOKIE_V3_KEY);
        assertEquals(pxContext.getPxCookieRaw(), CookieV3);
        assertEquals(pxContext.getS2sCallReason(), S2SCallReason.NONE.getValue());
    }

    @Test
    public void testVerifyFromTokensFirstCorrupted() throws PXException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        String cookies = "@$@#%" + "," + Constants.COOKIE_V1_KEY + ":" + CookieV1;
        enrichHttpRequestWithPxHeaders(request, Constants.MOBILE_SDK_AUTHORIZATION_HEADER, Constants.COOKIE_V3_KEY + ":" + CookieV3);
        enrichHttpRequestWithPxHeaders(request, Constants.MOBILE_SDK_TOKENS_HEADER, cookies);
        PXCookieValidator pxCookieValidator = new PXCookieValidator(pxConfiguration);
        PXContext pxContext = new PXContext(request, ipProvider, hostnameProvider, pxConfiguration);
        boolean verified = pxCookieValidator.verify(pxContext);
        assertTrue(verified);
        assertEquals(pxContext.getCookieVersion(), Constants.COOKIE_V1_KEY);
        assertEquals(pxContext.getPxCookieRaw(), CookieV1);
        assertEquals(pxContext.getS2sCallReason(), S2SCallReason.NONE.getValue());
    }

    @Test
    public void testVerifyFromTokensBothCorruptedSelectAuthHeaderCookie() throws PXException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        String cookies = "@$@#%" + "," + "@%@%@#";
        enrichHttpRequestWithPxHeaders(request, Constants.MOBILE_SDK_AUTHORIZATION_HEADER, Constants.COOKIE_V3_KEY + ":" + CookieV3);
        enrichHttpRequestWithPxHeaders(request, Constants.MOBILE_SDK_TOKENS_HEADER, cookies);
        PXCookieValidator pxCookieValidator = new PXCookieValidator(pxConfiguration);
        PXContext pxContext = new PXContext(request, ipProvider, hostnameProvider, pxConfiguration);
        boolean verified = pxCookieValidator.verify(pxContext);
        assertTrue(verified);
        assertEquals(pxContext.getCookieVersion(), Constants.COOKIE_V3_KEY);
        assertEquals(pxContext.getPxCookieRaw(), CookieV3);
        assertEquals(pxContext.getS2sCallReason(), S2SCallReason.NONE.getValue());
    }

    @Test
    public void testVerifyFromTokensBothCorruptedSelectOriginalTokenDecryptionError() throws PXException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        String cookies = "3:@$@#%" + "," + "1:@%@%@#";
        enrichHttpRequestWithPxHeaders(request, Constants.MOBILE_SDK_ORIGINAL_TOKENS_HEADER, Constants.COOKIE_V3_KEY + ":" + CookieV3);
        enrichHttpRequestWithPxHeaders(request, Constants.MOBILE_SDK_TOKENS_HEADER, cookies);
        PXCookieValidator pxCookieValidator = new PXCookieValidator(pxConfiguration);
        PXContext pxContext = new PXContext(request, ipProvider, hostnameProvider, pxConfiguration);
        boolean verified = pxCookieValidator.verify(pxContext);
        assertFalse(verified);
        assertEquals(pxContext.getCookieVersion(), Constants.COOKIE_V3_KEY);
        assertEquals(pxContext.getPxCookieRaw(), "_px=@%@%@#");
        assertEquals(pxContext.getS2sCallReason(), S2SCallReason.INVALID_DECRYPTION.getValue());
        assertEquals(pxContext.getDecodedOriginalToken(), PAYLOAD_V3);
        assertEquals(pxContext.getVid(), "84f7db40-9592-11e8-a7b3-5319fb36a9bf");
    }

    @Test
    public void testVerifyFromTokensNoneSelectOriginalTokenDecryptionError() throws PXException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        String cookies = "";
        enrichHttpRequestWithPxHeaders(request, Constants.MOBILE_SDK_ORIGINAL_TOKENS_HEADER, Constants.COOKIE_V3_KEY + ":" + CookieV3);
        enrichHttpRequestWithPxHeaders(request, Constants.MOBILE_SDK_TOKENS_HEADER, cookies);
        PXCookieValidator pxCookieValidator = new PXCookieValidator(pxConfiguration);
        PXContext pxContext = new PXContext(request, ipProvider, hostnameProvider, pxConfiguration);
        boolean verified = pxCookieValidator.verify(pxContext);
        assertFalse(verified);
        assertEquals(pxContext.getCookieVersion(), Constants.COOKIE_V3_KEY);
        assertEquals(pxContext.getPxCookieRaw(), null);
        assertEquals(pxContext.getS2sCallReason(), S2SCallReason.NO_COOKIE.getValue());
        assertEquals(pxContext.getDecodedOriginalToken(), PAYLOAD_V3);
        assertEquals(pxContext.getVid(), "84f7db40-9592-11e8-a7b3-5319fb36a9bf");
    }

    private void enrichHttpRequestWithPxHeaders(MockHttpServletRequest request, String headerKey, String headerValue) {
        request.addHeader(headerKey, headerValue);
    }

}
