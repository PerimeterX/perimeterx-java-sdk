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
import com.perimeterx.models.configuration.ModuleMode;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.exceptions.PXException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import testutils.TestObjectUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;


@Test
public class DefaultVerificationHandlerTest {

    private DefaultActivityHandler activityHandler;
    private IPProvider ipProvider;
    private HostnameProvider hostnameProvider;
    private PXConfiguration config;
    private DefaultVerificationHandler defaultVerificationHandler;
    private BlockHandler blockHandler;

    @BeforeMethod
    public void setUp() {
        config = TestObjectUtils.generateConfiguration();
        PXClient pxClient = TestObjectUtils.blockingPXClient(config.getBlockingScore());
        this.activityHandler = new DefaultActivityHandler(pxClient, config);
        this.hostnameProvider = new DefaultHostnameProvider();
        this.ipProvider = new RemoteAddressIPProvider();
        this.defaultVerificationHandler = new DefaultVerificationHandler(config, activityHandler);
    }

    @Test
    public void TestMonitorModeBypass() throws IOException, PXException {
        PXConfiguration config = PXConfiguration.builder()
                .appId("appId")
                .authToken("token")
                .cookieKey("cookieKey")
                .moduleMode(ModuleMode.MONITOR)
                .remoteConfigurationEnabled(false)
                .blockingScore(30)
                .bypassMonitorHeader("TEST-BYPASS")
                .build();
        HttpServletRequest request = new MockHttpServletRequest();
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
    public void TestMonitorModeBypassWrongValueInHeader() throws IOException, PXException {
        PXConfiguration config = PXConfiguration.builder()
                .appId("appId")
                .authToken("token")
                .cookieKey("cookieKey")
                .moduleMode(ModuleMode.MONITOR)
                .remoteConfigurationEnabled(false)
                .blockingScore(30)
                .bypassMonitorHeader("TEST-BYPASS")
                .build();
        HttpServletRequest request = new MockHttpServletRequest();
        ((MockHttpServletRequest) request).addHeader("TEST-BYPASS", "0");
        HttpServletResponseWrapper response = new HttpServletResponseWrapper(new MockHttpServletResponse());
        PXContext context = new PXContext(request, ipProvider, hostnameProvider, config);
        context.setRiskScore(100);
        DefaultVerificationHandler defaultVerificationHandler = new DefaultVerificationHandler(config, activityHandler);
        context.setBlockAction("b");
        boolean verified = defaultVerificationHandler.handleVerification(context, response);
        Assert.assertTrue(verified);
    }

    @Test
    public void TestMonitorModeBypassHeaderDefinedAndMissingFromRequest() throws IOException, PXException {
        PXConfiguration config = PXConfiguration.builder()
                .appId("appId")
                .authToken("token")
                .cookieKey("cookieKey")
                .moduleMode(ModuleMode.MONITOR)
                .remoteConfigurationEnabled(false)
                .blockingScore(30)
                .bypassMonitorHeader("TEST-BYPASS")
                .build();
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponseWrapper response = new HttpServletResponseWrapper(new MockHttpServletResponse());
        PXContext context = new PXContext(request, ipProvider, hostnameProvider, config);
        context.setRiskScore(100);
        DefaultVerificationHandler defaultVerificationHandler = new DefaultVerificationHandler(config, activityHandler);
        context.setBlockAction("b");
        boolean verified = defaultVerificationHandler.handleVerification(context, response);
        Assert.assertTrue(verified);
    }

}
