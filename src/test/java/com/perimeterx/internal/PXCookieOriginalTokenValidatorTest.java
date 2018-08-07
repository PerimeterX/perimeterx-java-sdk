package com.perimeterx.internal;

import com.perimeterx.api.providers.DefaultHostnameProvider;
import com.perimeterx.api.providers.RemoteAddressIPProvider;
import com.perimeterx.internals.PXCookieOriginalTokenValidator;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.utils.Constants;
import org.junit.Assert;
import org.springframework.mock.web.MockHttpServletRequest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test
public class PXCookieOriginalTokenValidatorTest {


    private final String COOKIE_KEY = "ASASDSAd32r223f2+wdsafsar2t43ASCASDFTfTt34gsfet3424tFSDFdsfsdr3R";
    private final String PAYLOAD = "{\"u\":\"484070ee-6783-44c8-86d1-2d9ca4a8eeb8\",\"v\":\"84f7db40-9592-11e8-a7b3-5319fb36a9bf\",\"t\":1533132216511,\"s\":0,\"a\":\"c\",\"sdk_version\":\"v1.5.4\"}";
    private PXConfiguration pxConfiguration;

    private String badXAuthorizationToken = "3:f97f176939becfc600134d9571512f5b6c5fce184c937068253726764c19ae59:QjQVxKnoGLwumB3hN73HV0MeiA495ta6JspXUSomjKla+CGPjeeYsL0fZozAyjPli28c+X8y2dKN7ZdCsq/e1w==:1000:B3+Q1h6aeaxgSm+SWKMpwL5ZIw+Sp9pwhsBqafFjGElEYVQucZtsAkBl6JUqafpdbkB5Bs61NdYsm9BUkorF9VZwICYsvPyvCjqQodKN1vCHGtO27EpBwZFCVRCcKOxtj4sIO4fwzvDnX2lDf/nCul12Dk2mD+rislTndlPRZh6ahPpe5rzX2/DsRNyXPMTL";
    private String encodedPayload =  "3:25676dd757cb796e1b4252a4d395e7dbdd7b36787ac9d5c884a52b8cc3d79cd5:ZzY2AgkAS3Dhu6BVQAyn5XxQXQIuYLmnklh0gzPTsif/ItN+kQq46jyq0YPXYahfstf/r4V+mkexvyPl4KKLeA==:1000:ZZht8m6lbnBHMeMtIvQKYSbbea6fiuIQyjxLZhgX9L5ODbE73qBBZ6FJSN5+NWCCzCNWnFAWItIA5e+4l3gi2B6ykh//KiDwqr5jw9XHACz4r/XEZvGtUoxUsPGC8sT9rjG1oWe/+omoWqSTAxJlXulcbtbhivy+Mlf+75Z7hT8gVsQr9aWXw1hsc2KlfifN";
    private DefaultHostnameProvider hostnameProvider;
    private RemoteAddressIPProvider ipProvider;


    @BeforeClass
    public void init(){
        pxConfiguration = new PXConfiguration.Builder()
                .appId("APP_ID")
                .authToken("AUTH_123")
                .cookieKey(COOKIE_KEY)
                .build();
        hostnameProvider = new DefaultHostnameProvider();
        ipProvider = new RemoteAddressIPProvider();
    }

    private void enrichHttpRequestWithPxHeaders(MockHttpServletRequest request, String headerKey, String headerValue) {
        request.addHeader(headerKey, headerValue);
    }


    @Test
    public void testPxCookieOriginalTokenValidatorWithOriginalToken(){
        MockHttpServletRequest request = new MockHttpServletRequest();
        enrichHttpRequestWithPxHeaders(request, Constants.MOBILE_SDK_HEADER, Constants.MOBILE_ERROR_NO_CONNECTION);
        enrichHttpRequestWithPxHeaders(request, Constants.MOBILE_SDK_ORIGINAL_TOKEN_HEADER, encodedPayload);
        PXContext pxContext = new PXContext(request, ipProvider, hostnameProvider, pxConfiguration);
        pxContext.setDeserializeFromOriginalToken(true);
        PXCookieOriginalTokenValidator pxCookieOriginalTokenValidator = new PXCookieOriginalTokenValidator();
        pxCookieOriginalTokenValidator.verify(pxConfiguration, pxContext);
        Assert.assertEquals(pxContext.getOriginalToken(), encodedPayload);
        Assert.assertEquals(pxContext.getVid(),"84f7db40-9592-11e8-a7b3-5319fb36a9bf");
        Assert.assertEquals(pxContext.getOriginalUuid(),"484070ee-6783-44c8-86d1-2d9ca4a8eeb8");
        Assert.assertEquals(pxContext.getDecodedOriginalToken(),PAYLOAD);
    }

    @Test
    public void testPxCookieOriginalTokenValidatorBadOriginalToken(){
        MockHttpServletRequest request = new MockHttpServletRequest();
        enrichHttpRequestWithPxHeaders(request, Constants.MOBILE_SDK_HEADER, Constants.MOBILE_ERROR_NO_CONNECTION);
        enrichHttpRequestWithPxHeaders(request, Constants.MOBILE_SDK_ORIGINAL_TOKEN_HEADER, badXAuthorizationToken);
        PXContext pxContext = new PXContext(request, ipProvider, hostnameProvider, pxConfiguration);
        pxContext.setDeserializeFromOriginalToken(true);
        PXCookieOriginalTokenValidator pxCookieOriginalTokenValidator = new PXCookieOriginalTokenValidator();
        pxCookieOriginalTokenValidator.verify(pxConfiguration, pxContext);
        Assert.assertEquals(pxContext.getOriginalToken(), badXAuthorizationToken);
        Assert.assertNull(pxContext.getVid());
        Assert.assertNull(pxContext.getOriginalUuid());
        Assert.assertNull(pxContext.getDecodedOriginalToken());
    }


}
