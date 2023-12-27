package com.perimeterx.internal;

import com.perimeterx.api.PerimeterX;
import com.perimeterx.api.activities.BufferedActivityHandler;
import com.perimeterx.http.PXClient;
import com.perimeterx.http.PXHttpClient;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.activities.EnforcerTelemetry;
import com.perimeterx.models.activities.UpdateReason;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.utils.Base64;
import com.perimeterx.utils.HMACUtils;
import com.perimeterx.utils.StringUtils;
import com.perimeterx.utils.logger.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import testutils.ConfiguredTest;
import testutils.TestObjectUtils;

import javax.servlet.http.HttpServletResponseWrapper;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static com.perimeterx.utils.Constants.DEFAULT_TELEMETRY_REQUEST_HEADER_NAME;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class TelemetryTest extends ConfiguredTest {
    public static final int MS_IN_DAY = 86400000;
    private static final String VALID_EXPIRATION_TIME = String.valueOf(System.currentTimeMillis() + MS_IN_DAY);

    private BufferedActivityHandler activityHandler;
    private PXConfiguration configuration;
    private PXHttpClient pxHttpClient;
    private PerimeterX perimeterx;
    private HttpServletResponseWrapper mockHttpResponse;

    @BeforeMethod
    public void testSetup() {
        this.configuration = TestObjectUtils.generateConfiguration();
        PXClient pxClient = TestObjectUtils.blockingPXClient(configuration.getBlockingScore());
        this.activityHandler = new BufferedActivityHandler(pxClient, configuration);
        this.pxHttpClient = mock(PXHttpClient.class);
        this.mockHttpResponse = new HttpServletResponseWrapper(new MockHttpServletResponse());

        try {
            this.perimeterx = TestObjectUtils.testablePerimeterXObject(this.configuration, this.pxHttpClient);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testIsValidTelemetryRequestWithValidHeader() throws Exception {
        final String encodedHmac = encodeHmac(this.configuration.getCookieKey(), VALID_EXPIRATION_TIME);

        Assert.assertTrue(isValidTelemetryRequest(encodedHmac));
    }

    @Test
    public void testIsValidTelemetryRequestWithInvalidCookieSecret() throws Exception {
        final String wrongCookieSecret = "wrongCookie";
        final String encodedHmac = encodeHmac(wrongCookieSecret, VALID_EXPIRATION_TIME);

        Assert.assertFalse(isValidTelemetryRequest(encodedHmac));
    }

    @Test
    public void testIsValidTelemetryRequestWithInvalidTimestamp() throws Exception {
        final String invalidExpiredTime = String.valueOf(System.currentTimeMillis() - MS_IN_DAY);
        final String encodedHmac = encodeHmac(this.configuration.getCookieKey(), invalidExpiredTime);

        Assert.assertFalse(isValidTelemetryRequest(encodedHmac));
    }

    @Test
    public void testHandleTelemetryActivity() {
        boolean thrown = false;

        try {
            activityHandler.handleEnforcerTelemetryActivity(this.configuration, UpdateReason.COMMAND, new PXContext(new LoggerFactory()));
        } catch (Exception e) {
            thrown = true;
        }

        Assert.assertFalse(thrown);
    }

    @Test
    public void testSendTelemetryWithValidHeader() throws Exception {
        final String validHmac = encodeHmac(this.configuration.getCookieKey(), VALID_EXPIRATION_TIME);
        this.perimeterx.pxVerify(getMockHttpRequestWithTelemetryHeader(validHmac), this.mockHttpResponse);

        verify(this.pxHttpClient, times(1)).sendEnforcerTelemetry(any(EnforcerTelemetry.class), any(PXContext.class));
    }

    @Test
    public void testWontSendTelemetryActivityWithInvalidHeader() throws Exception {
        final String invalidExpirationTime = String.valueOf(System.currentTimeMillis() - MS_IN_DAY);
        final String encodedHmac = encodeHmac(this.configuration.getCookieKey(), invalidExpirationTime);
        this.perimeterx.pxVerify(getMockHttpRequestWithTelemetryHeader(encodedHmac), this.mockHttpResponse);

        verify(this.pxHttpClient, never()).sendEnforcerTelemetry(any(EnforcerTelemetry.class), any(PXContext.class));
    }

    @Test
    public void testWontSendTelemetryActivityWithNoTelemetryHeader() throws Exception {
        this.perimeterx.pxVerify(new MockHttpServletRequest(), this.mockHttpResponse);

        verify(this.pxHttpClient, never()).sendEnforcerTelemetry(any(EnforcerTelemetry.class), any(PXContext.class));
    }

    private MockHttpServletRequest getMockHttpRequestWithTelemetryHeader(String headerValue) {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(DEFAULT_TELEMETRY_REQUEST_HEADER_NAME, headerValue);

        return request;
    }

    private String encodeHmac(String cookieSecret, String expiredTime) throws NoSuchAlgorithmException, InvalidKeyException {
        final byte[] hmacBytes = HMACUtils.HMACString(expiredTime, cookieSecret);
        final String generatedHmac = StringUtils.byteArrayToHexString(hmacBytes).toLowerCase();

        return Base64.encodeToString((expiredTime + ":" + generatedHmac).getBytes(), false);
    }

    private boolean isValidTelemetryRequest(String encodedHmac) {
        MockHttpServletRequest request = getMockHttpRequestWithTelemetryHeader(encodedHmac);

        return this.perimeterx.isValidTelemetryRequest(request, new PXContext(new LoggerFactory()));
    }
}
