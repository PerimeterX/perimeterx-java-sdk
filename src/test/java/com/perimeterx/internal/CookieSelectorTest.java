package com.perimeterx.internal;

import com.perimeterx.api.providers.DefaultHostnameProvider;
import com.perimeterx.api.providers.RemoteAddressIPProvider;
import com.perimeterx.internals.CookieSelector;
import com.perimeterx.internals.cookie.AbstractPXCookie;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.exceptions.PXCookieDecryptionException;
import com.perimeterx.models.exceptions.PXException;
import com.perimeterx.models.risk.S2SCallReason;
import com.perimeterx.utils.Constants;
import org.springframework.mock.web.MockHttpServletRequest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class CookieSelectorTest {
    private final String COOKIE_KEY = "ASASDSAd32r223f2+wdsafsar2t43ASCASDFTfTt34gsfet3424tFSDFdsfsdr3R";
    private DefaultHostnameProvider hostnameProvider;
    private RemoteAddressIPProvider ipProvider;
    private String CookieV3 = "3:25676dd757cb796e1b4252a4d395e7dbdd7b36787ac9d5c884a52b8cc3d79cd5:ZzY2AgkAS3Dhu6BVQAyn5XxQXQIuYLmnklh0gzPTsif/ItN+kQq46jyq0YPXYahfstf/r4V+mkexvyPl4KKLeA==:1000:ZZht8m6lbnBHMeMtIvQKYSbbea6fiuIQyjxLZhgX9L5ODbE73qBBZ6FJSN5+NWCCzCNWnFAWItIA5e+4l3gi2B6ykh//KiDwqr5jw9XHACz4r/XEZvGtUoxUsPGC8sT9rjG1oWe/+omoWqSTAxJlXulcbtbhivy+Mlf+75Z7hT8gVsQr9aWXw1hsc2KlfifN";
    private String CookieV1 = "1:eGPYisZ+3qU=:1000:7igwrWkws2WEaST6gH9QyFzDWzlLFmR8Y50DG2ZUwCNjdvaZ6niEBqqI68whIuA96G481qX48WSV9GGv0bAhIslbNeZUjpbndMJIAF8O4MqnKTHEVzTGzCCKDfHFIlAl3eCvvvhddhZLMYJUI1tA6lWkO5zHQd33O4w7IQ3DPwqKpe7lxQVHSNPA/emFNG6o/g4gz7RTP9FQep6SW8GuEdHXv4a2EIvaqRZlOfyky0vTeS5cgiSblu2neDe4ntB3Xm5s7XN7+mjhPjTGoEfZCA==";
    private final String PAYLOAD_V3 = "{\"u\":\"484070ee-6783-44c8-86d1-2d9ca4a8eeb8\",\"v\":\"84f7db40-9592-11e8-a7b3-5319fb36a9bf\",\"t\":1533132216511,\"s\":0,\"a\":\"c\",\"sdk_version\":\"v1.5.4\"}";
    private final String PAYLOAD_V1 = "{\"v\":\"b844b860-92fe-11e8-9ad2-075a911258e2\",\"u\":\"e87f6f10-9fb6-11e8-9d8d-572ad86677e3\",\"s\":{\"a\":0,\"b\":0},\"t\":1534247356969,\"h\":\"5adab483c4c6157072a7af160a48018946cc719df7f5b54588fe07c85a56e65e\"}";
    private PXConfiguration pxConfiguration;

    @BeforeClass
    public void init() {
        pxConfiguration = new PXConfiguration.Builder()
                .appId("APP_ID")
                .authToken("AUTH_123")
                .cookieKey(COOKIE_KEY)
                .build();
        hostnameProvider = new DefaultHostnameProvider();
        ipProvider = new RemoteAddressIPProvider();
    }

    @Test
    public void testSelectFromTokensHappyFlow() throws PXCookieDecryptionException, PXException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        enrichHttpRequestWithPxHeaders(request, Constants.MOBILE_SDK_AUTHORIZATION_HEADER, CookieV3);
        enrichHttpRequestWithPxHeaders(request, Constants.MOBILE_SDK_TOKENS_HEADER, CookieV3 + "," + CookieV1);
        PXContext context = new PXContext(request, ipProvider, hostnameProvider, pxConfiguration);
        AbstractPXCookie cookie = CookieSelector.selectFromTokens(context, pxConfiguration);
        assertEquals(cookie.getDecodedCookie().toString(), PAYLOAD_V3);
    }

    @Test
    public void testSelectFromTokensFallbackToAuthHeader() throws PXCookieDecryptionException, PXException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        enrichHttpRequestWithPxHeaders(request, Constants.MOBILE_SDK_TOKENS_HEADER, "!@%@#%");
        enrichHttpRequestWithPxHeaders(request, Constants.MOBILE_SDK_AUTHORIZATION_HEADER, CookieV1);
        PXContext context = new PXContext(request, ipProvider, hostnameProvider, pxConfiguration);
        AbstractPXCookie cookie = CookieSelector.selectFromTokens(context, pxConfiguration);
        assertEquals(cookie.getDecodedCookie().toString(), PAYLOAD_V1);
        assertEquals(context.getS2sCallReason(), S2SCallReason.NONE.getValue());
    }

    @Test
    public void testSelectFromOriginalTokens() throws PXCookieDecryptionException, PXException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        enrichHttpRequestWithPxHeaders(request, Constants.MOBILE_SDK_ORIGINAL_TOKENS_HEADER, CookieV3 + "," + CookieV1);
        enrichHttpRequestWithPxHeaders(request, Constants.MOBILE_SDK_ORIGINAL_TOKEN_HEADER, CookieV3);
        enrichHttpRequestWithPxHeaders(request, Constants.MOBILE_SDK_TOKENS_HEADER, "2");
        PXContext context = new PXContext(request, ipProvider, hostnameProvider, pxConfiguration);
        AbstractPXCookie cookie = CookieSelector.selectOriginalTokens(context, pxConfiguration);
        assertEquals(PAYLOAD_V3, cookie.getDecodedCookie().toString());
        assertEquals(context.getOriginalTokenError(), "");
    }

    @Test
    public void testSelectFromOriginalTokensFallbackToOriginalToken() throws PXCookieDecryptionException, PXException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        enrichHttpRequestWithPxHeaders(request, Constants.MOBILE_SDK_ORIGINAL_TOKENS_HEADER, "!@%@#%");
        enrichHttpRequestWithPxHeaders(request, Constants.MOBILE_SDK_ORIGINAL_TOKEN_HEADER, CookieV3);
        enrichHttpRequestWithPxHeaders(request, Constants.MOBILE_SDK_TOKENS_HEADER, "2");
        PXContext context = new PXContext(request, ipProvider, hostnameProvider, pxConfiguration);
        AbstractPXCookie cookie = CookieSelector.selectOriginalTokens(context, pxConfiguration);
        assertEquals(PAYLOAD_V3, cookie.getDecodedCookie().toString());
        assertEquals(context.getOriginalTokenError(), "");
    }

    @Test
    public void testSelectFromOriginalTokensFirstCookieFailed() throws PXCookieDecryptionException, PXException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        enrichHttpRequestWithPxHeaders(request, Constants.MOBILE_SDK_ORIGINAL_TOKENS_HEADER, "!@%@#%," + CookieV1);
        enrichHttpRequestWithPxHeaders(request, Constants.MOBILE_SDK_ORIGINAL_TOKEN_HEADER, CookieV3);
        enrichHttpRequestWithPxHeaders(request, Constants.MOBILE_SDK_TOKENS_HEADER, "2");
        PXContext context = new PXContext(request, ipProvider, hostnameProvider, pxConfiguration);
        AbstractPXCookie cookie = CookieSelector.selectOriginalTokens(context, pxConfiguration);
        assertEquals(PAYLOAD_V1, cookie.getDecodedCookie().toString());
        assertEquals(cookie.getCookieVersion(), Constants.COOKIE_V1_KEY);
        assertEquals(context.getOriginalTokenError(), "");
    }

    @Test
    public void testSelectFromTokensFirstCookieFailed() throws PXCookieDecryptionException, PXException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        enrichHttpRequestWithPxHeaders(request, Constants.MOBILE_SDK_AUTHORIZATION_HEADER, CookieV3);
        enrichHttpRequestWithPxHeaders(request, Constants.MOBILE_SDK_TOKENS_HEADER, "@#@#$@#," + CookieV1);
        PXContext context = new PXContext(request, ipProvider, hostnameProvider, pxConfiguration);
        AbstractPXCookie cookie = CookieSelector.selectFromTokens(context, pxConfiguration);
        assertEquals(PAYLOAD_V1, cookie.getDecodedCookie().toString());
        assertEquals(context.getS2sCallReason(), S2SCallReason.NONE.getValue());
    }

    private void enrichHttpRequestWithPxHeaders(MockHttpServletRequest request, String headerKey, String headerValue) {
        request.addHeader(headerKey, headerValue);
    }

}
