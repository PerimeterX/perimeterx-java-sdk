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
 * Created by saar shtalryd on 08/01/2018.
 */
@Test
public class CookieV3MobileTest {
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
    public void testCookieV3MobileToken() {
        String pxCookie = "3:2402d0b20e83c4e2517b39cb09ae59ad212abb362095c51de1d100fe91104701:9T6dRNxqkrw=:1000:IddId3O2fmSlorPoxtg8Sfdll9K0wbP4LXaEUCByLpU12gcZriwwwsGLGxG31uePy1nsOAwHze22rw6TAHR77+zfrSTASxYR5jODn0Nr+sYJm4GVT9OkUHpq0Z5JzgdpMBuzgsLGKQcKMcajHIzWGNzSEWjcHKRHHqL153w7PSJ0IEOj/hNWHKWfuLICZ4nS";
        ((MockHttpServletRequest) request).addHeader("x-px-authorization", pxCookie);
        this.context = new PXContext(request, ipProvider, hostnameProvider, pxConfiguration);
        assertTrue(this.context.isMobileToken());
    }


    @Test
    public void testCookieV3MobilePass() {
        String pxCookie = "3:2402d0b20e83c4e2517b39cb09ae59ad212abb362095c51de1d100fe91104701:9T6dRNxqkrw=:1000:IddId3O2fmSlorPoxtg8Sfdll9K0wbP4LXaEUCByLpU12gcZriwwwsGLGxG31uePy1nsOAwHze22rw6TAHR77+zfrSTASxYR5jODn0Nr+sYJm4GVT9OkUHpq0Z5JzgdpMBuzgsLGKQcKMcajHIzWGNzSEWjcHKRHHqL153w7PSJ0IEOj/hNWHKWfuLICZ4nS";
        ((MockHttpServletRequest) request).addHeader("x-px-authorization", pxCookie);
        this.context = new PXContext(request, ipProvider, hostnameProvider, pxConfiguration);
        boolean verify = cookieValidator.verify(pxConfiguration, context);
        assertTrue(verify);
    }

    @Test
    public void testCookieV3MobileFailOnSensitiveRoute() {
        PXConfiguration configuration = new PXConfiguration.Builder()
                .cookieKey("COOKIE_KEY_STRING_MOBILE")
                .appId("APP_ID")
                .authToken("AUTH_TOKEN")
                .sensitiveRoutes(new HashSet(Arrays.asList("/login")))
                .build();
        String pxCookie = "3:2402d0b20e83c4e2517b39cb09ae59ad212abb362095c51de1d100fe91104701:9T6dRNxqkrw=:1000:IddId3O2fmSlorPoxtg8Sfdll9K0wbP4LXaEUCByLpU12gcZriwwwsGLGxG31uePy1nsOAwHze22rw6TAHR77+zfrSTASxYR5jODn0Nr+sYJm4GVT9OkUHpq0Z5JzgdpMBuzgsLGKQcKMcajHIzWGNzSEWjcHKRHHqL153w7PSJ0IEOj/hNWHKWfuLICZ4nS";
        ((MockHttpServletRequest) request).setRequestURI("/login/user");
        ((MockHttpServletRequest) request).addHeader("x-px-authorization", pxCookie);
        this.context = new PXContext(request, ipProvider, hostnameProvider, configuration);
        assertTrue(this.context.isSensitiveRoute());
        boolean verify = cookieValidator.verify(pxConfiguration, context);
        assertFalse(verify);
        assertEquals(S2SCallReason.SENSITIVE_ROUTE.name(), context.getS2sCallReason());
    }

    @Test
    public void testCookieV3MobileFailOnDecryption() {
        String pxCookie = "3:2402d0b20e83c4e2517b39cb09ae59ad212abb362095c51de1d100fe91104701:9T6dRNxqkrw=:1000:IddId3O2fmSlorPoxtg8Sfdll9K0wbP4LXaEUCByLpU12gcZriwwwsGLGxG31uePy1nsOAwHze22rw6TAHR77+zfrSTASxYR5jODn0Nr+sYJm4GVT9OkUHpq0Z5JzgdpMBuzgsLGKQcKMcajHIzWGNzSEWjcHKRHHqL153w7PSJ0IEOj/hNWHKWfuLICZ4nS";
        ((MockHttpServletRequest) request).addHeader("x-px-authorization", pxCookie);
        this.pxConfiguration = new PXConfiguration.Builder()
                .cookieKey("INVALID COOKIE TOKEN")
                .appId("APP_ID")
                .authToken("AUTH_TOKEN")
                .build();

        this.context = new PXContext(request, ipProvider, hostnameProvider, pxConfiguration);
        boolean verify = cookieValidator.verify(pxConfiguration, context);
        assertFalse(verify);
        assertEquals(S2SCallReason.INVALID_DECRYPTION.name(), context.getS2sCallReason());
    }

    @Test
    public void testCookieV3MobileMobileFailOnFakeCookie() {
        String pxCookie = "3:bavs";
        ((MockHttpServletRequest) request).addHeader("x-px-authorization", pxCookie);
        this.context = new PXContext(request, ipProvider, hostnameProvider, pxConfiguration);
        boolean verify = cookieValidator.verify(pxConfiguration, context);

        assertFalse(verify);
        assertEquals(S2SCallReason.INVALID_DECRYPTION.name(), context.getS2sCallReason());
    }

    @Test
    public void testCookieV3MobileFailOnCookieExpired() {
        String pxCookie = "3:3460d45c0468b7c86cadb0db00e291ab5583f95a0c750190867175786774aeeb:XxUC0J5CZ7k=:1000:13m6JMqXMnWJk04EkqAmeaTydu075jf0zqGJLX0CAzdrksCmIajvaAlHTpJSFy6nze7+Vs7MdlKDxsEVrIh2iHQnMgqQzTrGmbyHu3nyCrPfPJfB+ZxiqS4rmrwKDLvoki1TFcmkHg4hp9YhPsBYCHajzj3scxj4/TlASzEe6pY=";
        ((MockHttpServletRequest) request).addHeader("x-px-authorization", pxCookie);
        this.context = new PXContext(request, ipProvider, hostnameProvider, pxConfiguration);
        boolean verify = cookieValidator.verify(pxConfiguration, context);

        assertFalse(verify);
        assertEquals(S2SCallReason.COOKIE_EXPIRED.name(), context.getS2sCallReason());
    }

    @Test
    public void testCookieV3MobilePassAndHighScore() {
        String pxCookie = "3:32b571e119f9951d4bde6bb53f4f971cec5fd05b461d6e0c4371dcc05755d5ca:TQl+oseKQSI=:1000:54tfSCowPsmU1IsVSsKXF3RFON54eyFnkt1exA9Jm8BPtPm9VMKJxDjDc4hGnLVmUASMBEpbCJzgA9RQd8BI77WVLiAiKQQ+q4/WduwA1ubaIY5hZWvEw/nMVYRF/bF8XgbpIjcxabPSlDi2TF+phCxTMNRNxE7Ha5srQws0c39ClhEOb8qc0wUBZkhwTC3Z";
        ((MockHttpServletRequest) request).addHeader("x-px-authorization", pxCookie);
        this.context = new PXContext(request, ipProvider, hostnameProvider, pxConfiguration);
        boolean verify = cookieValidator.verify(pxConfiguration, context);

        assertTrue(verify);
        assertEquals(BlockReason.COOKIE, context.getBlockReason());
    }

    @Test
    public void testCookieV3MobilePassLowScore() {
        String pxCookie = "3:3ffb041472b9ce834a0e0df743a2ede3a24693f9ceabfbec37e79568675f16b6:mHmraarkLt4=:1000:E9Y0w+hz1J6jMTlupT5BrSxrabi4McPBtcar2YmF1XaI4XIaY3Q2mRardGnSVTPQ3AUKbCdQCCASjeTsq7yI1zhHlcldtxNHmpehNsNzidOhTux2V+UzIn+YJ9oaKySHAmYxrou4rc/F23bPW3hhZfqmZxwYzjmo+xCRiZViuqWNUBGHIdDbCqlIi57C4Gt8";
        ((MockHttpServletRequest) request).addHeader("x-px-authorization", pxCookie);
        this.context = new PXContext(request, ipProvider, hostnameProvider, pxConfiguration);
        boolean verify = cookieValidator.verify(pxConfiguration, context);

        assertTrue(verify);
        assertEquals(BlockReason.NONE, context.getBlockReason());
        assertEquals(S2SCallReason.NONE.name(), context.getS2sCallReason());
    }
}
