package com.perimeterx.internal;

import com.perimeterx.api.PerimeterX;
import com.perimeterx.api.activities.BufferedActivityHandler;
import com.perimeterx.http.PXClient;
import com.perimeterx.models.activities.UpdateReason;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.utils.Base64;
import com.perimeterx.utils.HMACUtils;
import com.perimeterx.utils.StringUtils;
import org.springframework.mock.web.MockHttpServletRequest;
import org.testng.Assert;
import org.testng.annotations.Test;
import testutils.ConfiguredTest;
import testutils.TestObjectUtils;

import static com.perimeterx.utils.Constants.DEFAULT_TELEMETRY_REQUEST_HEADER_NAME;

public class TelemetryTest extends ConfiguredTest {
    private BufferedActivityHandler activityHandler;
    private PXConfiguration config;
    private PXConfiguration configuration;

    @Override
    public void testSetup() {
        configuration = TestObjectUtils.generateConfiguration();
        config = TestObjectUtils.generateConfiguration();
        PXClient pxClient = TestObjectUtils.blockingPXClient(config.getBlockingScore());
        this.activityHandler = new BufferedActivityHandler(pxClient, config);
    }


    @Test
    public void testPxTelemetryRequestWithValidHeader() throws Exception {
        final String expiredTime =  String.valueOf(System.currentTimeMillis() + 86400000);
        final byte[] hmacBytes = HMACUtils.HMACString(expiredTime, configuration.getCookieKey());
        final String generatedHmac = StringUtils.byteArrayToHexString(hmacBytes).toLowerCase();
        final String encodedHmac = Base64.encodeToString((expiredTime + ":" + generatedHmac).getBytes(), false);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(DEFAULT_TELEMETRY_REQUEST_HEADER_NAME, encodedHmac);
        PXClient client = TestObjectUtils.blockingPXClient(configuration.getBlockingScore());
        PerimeterX perimeterx = TestObjectUtils.testablePerimeterXObject(configuration, client);

        Assert.assertTrue(perimeterx.isTelemetryRequest(request));
    }

    @Test
    public void testPxTelemetryRequestWithInvalidHeader() throws Exception {
        final String expiredTime =  String.valueOf(System.currentTimeMillis() + 86400000);
        final String wrongCookieSecret = "wrongCookie";
        final byte[] hmacBytes = HMACUtils.HMACString(expiredTime, wrongCookieSecret);
        final String generatedHmac = StringUtils.byteArrayToHexString(hmacBytes).toLowerCase();
        final String encodedHmac = Base64.encodeToString((expiredTime + ":" + generatedHmac).getBytes(), false);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(DEFAULT_TELEMETRY_REQUEST_HEADER_NAME, encodedHmac);
        PXClient client = TestObjectUtils.blockingPXClient(configuration.getBlockingScore());
        PerimeterX perimeterx = TestObjectUtils.testablePerimeterXObject(configuration, client);

        Assert.assertFalse(perimeterx.isTelemetryRequest(request));
    }

    @Test
    public void testHandlePageRequestedActivity() {
        boolean thrown = false;

        try {
            activityHandler.handleEnforcerTelemetryActivity(configuration, UpdateReason.TELEMETRY);
        } catch (Exception e) {
            thrown = true;
        }

        Assert.assertFalse(thrown);
    }
}
