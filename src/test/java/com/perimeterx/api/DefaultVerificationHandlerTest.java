package com.perimeterx.api;

import com.perimeterx.api.activities.DefaultActivityHandler;
import com.perimeterx.api.blockhandler.BlockHandler;
import com.perimeterx.api.providers.DefaultHostnameProvider;
import com.perimeterx.api.providers.HostnameProvider;
import com.perimeterx.api.providers.IPProvider;
import com.perimeterx.api.providers.RemoteAddressIPProvider;
import com.perimeterx.api.verificationhandler.DefaultVerificationHandler;
import com.perimeterx.http.PXClient;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.activities.Activity;
import com.perimeterx.models.activities.BlockActivityDetails;
import com.perimeterx.models.configuration.ModuleMode;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.exceptions.PXException;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import testutils.CustomBlockHandler;
import testutils.TestObjectUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


@Test
public class DefaultVerificationHandlerTest {

    private DefaultActivityHandler activityHandler;
    private IPProvider ipProvider;
    private HostnameProvider hostnameProvider;
    private PXConfiguration config;
    private DefaultVerificationHandler defaultVerificationHandler;
    private BlockHandler blockHandler;
    private HttpServletRequest request;
    private HttpServletResponseWrapper response;

    @BeforeMethod
    public void setUp() {
        this.config = PXConfiguration.builder()
                .appId("appId")
                .authToken("token")
                .cookieKey("cookieKey")
                .bypassMonitorHeader("TEST-BYPASS")
                .build();
        this.request = new MockHttpServletRequest();
        this.response = new HttpServletResponseWrapper(new MockHttpServletResponse());
        PXClient pxClient = TestObjectUtils.blockingPXClient(config.getBlockingScore());
        this.activityHandler = new DefaultActivityHandler(pxClient, config);
        this.hostnameProvider = new DefaultHostnameProvider();
        this.ipProvider = new RemoteAddressIPProvider();
        this.defaultVerificationHandler = new DefaultVerificationHandler(config, activityHandler);
    }

    @Test
    public void TestMonitorModeBypass() throws PXException {
        ((MockHttpServletRequest) request).addHeader("TEST-BYPASS", "1");
        HttpServletResponseWrapper response = new HttpServletResponseWrapper(new MockHttpServletResponse());
        PXContext context = new PXContext(request, ipProvider, hostnameProvider, config);
        context.setRiskScore(100);
        DefaultVerificationHandler defaultVerificationHandler = new DefaultVerificationHandler(config, activityHandler);
        context.setBlockAction("b");
        boolean verified = defaultVerificationHandler.handleVerification(context, response);
        Assert.assertFalse(verified);
    }

    @Test
    public void TestMonitorModeBypassWrongValueInHeader() throws PXException {
        ((MockHttpServletRequest) request).addHeader("TEST-BYPASS", "0");
        PXContext context = new PXContext(request, ipProvider, hostnameProvider, config);
        context.setRiskScore(100);
        DefaultVerificationHandler defaultVerificationHandler = new DefaultVerificationHandler(config, activityHandler);
        context.setBlockAction("b");
        boolean verified = defaultVerificationHandler.handleVerification(context, response);
        Assert.assertTrue(verified);
    }

    @Test
    public void TestMonitorModeBypassHeaderDefinedAndMissingFromRequest() throws PXException {
        HttpServletResponseWrapper response = new HttpServletResponseWrapper(new MockHttpServletResponse());
        PXContext context = new PXContext(request, ipProvider, hostnameProvider, config);
        context.setRiskScore(100);
        DefaultVerificationHandler defaultVerificationHandler = new DefaultVerificationHandler(config, activityHandler);
        context.setBlockAction("b");
        boolean verified = defaultVerificationHandler.handleVerification(context, response);
        Assert.assertTrue(verified);
    }

    @Test
    public void TestIsBlockHandlerIsPreviousToActivityHandlers() throws PXException, IOException {
        config = PXConfiguration.builder()
                .appId("appId")
                .authToken("token")
                .cookieKey("cookieKey")
                .moduleMode(ModuleMode.BLOCKING)
                .blockHandler(new CustomBlockHandler())
                .build();
        final HttpServletResponseWrapper response = new HttpServletResponseWrapper(new MockHttpServletResponse());
        final PXContext context = new PXContext(request, ipProvider, hostnameProvider, config);
        context.setRiskScore(100);
        context.setBlockAction("b");

        final PXClient pxClient = mock(PXClient.class);
        activityHandler = new DefaultActivityHandler(pxClient, config);
        final DefaultVerificationHandler defaultVerificationHandler = new DefaultVerificationHandler(config, activityHandler);

        defaultVerificationHandler.handleVerification(context, response);
        final ArgumentCaptor<Activity> captor = ArgumentCaptor.forClass(Activity.class);
        verify(pxClient).sendActivity(captor.capture());

        Assert.assertTrue((((BlockActivityDetails) captor.getValue().getDetails()).getSimulatedBlock()));
    }
}
