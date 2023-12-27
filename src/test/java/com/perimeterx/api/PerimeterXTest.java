package com.perimeterx.api;

import com.perimeterx.http.PXClient;
import com.perimeterx.http.ResponseWrapper;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.utils.JSONUtilsTest;
import com.perimeterx.utils.logger.ConsoleLogger;
import com.perimeterx.utils.logger.LoggerFactory;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.testng.Assert;
import org.testng.annotations.Test;
import testutils.ConfiguredTest;
import testutils.TestObjectUtils;

import static com.perimeterx.utils.Constants.LOGGER_TOKEN_HEADER_NAME;
import static org.mockito.Mockito.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Created by shikloshi on 13/07/2016.
 */

@Test
public class PerimeterXTest extends ConfiguredTest {

    private PXConfiguration configuration;

    @Override
    public void testSetup() throws Exception {
        configuration = TestObjectUtils.generateConfiguration();
    }

    @Test
    public void testTelemetryObject() {
        PXConfiguration clonedConfig = configuration.getTelemetryConfig();
        Assert.assertTrue(!clonedConfig.equals(configuration));

        Assert.assertTrue(!configuration.getAuthToken().equals(clonedConfig.getAuthToken()));
        Assert.assertTrue(!configuration.getCookieKey().equals(clonedConfig.getCookieKey()));

        Assert.assertEquals(clonedConfig.getAppId(), configuration.getAppId());
        Assert.assertEquals(clonedConfig.isModuleEnabled(), configuration.isModuleEnabled());
        Assert.assertEquals(clonedConfig.isEncryptionEnabled(), configuration.isEncryptionEnabled());
        Assert.assertEquals(clonedConfig.getBlockingScore(), configuration.getBlockingScore());
        Assert.assertTrue(compareCollections(clonedConfig.getSensitiveHeaders(), configuration.getSensitiveHeaders()));
        Assert.assertEquals(clonedConfig.getMaxBufferLen(), configuration.getMaxBufferLen());
        Assert.assertEquals(clonedConfig.getApiTimeout(), configuration.getApiTimeout());
        Assert.assertEquals(clonedConfig.getConnectionTimeout(), configuration.getConnectionTimeout());
        Assert.assertEquals(clonedConfig.isSendPageActivities(), configuration.isSendPageActivities());
        Assert.assertEquals(clonedConfig.isSignedWithIP(), configuration.isSignedWithIP());
        Assert.assertEquals(clonedConfig.getServerURL(), configuration.getServerURL());
        Assert.assertEquals(clonedConfig.getCustomLogo(), configuration.getCustomLogo());
        Assert.assertEquals(clonedConfig.getCssRef(), configuration.getCssRef());
        Assert.assertEquals(clonedConfig.getJsRef(), configuration.getJsRef());
        Assert.assertTrue(compareCollections(clonedConfig.getSensitiveRoutes(), configuration.getSensitiveRoutes()));
        Assert.assertTrue(compareCollections(clonedConfig.getIpHeaders(), configuration.getIpHeaders()));
        Assert.assertEquals(clonedConfig.getChecksum(), configuration.getChecksum());
        Assert.assertEquals(clonedConfig.isRemoteConfigurationEnabled(), configuration.isRemoteConfigurationEnabled());
        Assert.assertEquals(clonedConfig.getModuleMode(), configuration.getModuleMode());
        Assert.assertEquals(clonedConfig.getRemoteConfigurationInterval(), configuration.getRemoteConfigurationInterval());
        Assert.assertEquals(clonedConfig.getRemoteConfigurationDelay(), configuration.getRemoteConfigurationDelay());
        Assert.assertEquals(clonedConfig.getMaxConnections(), configuration.getMaxConnections());
        Assert.assertEquals(clonedConfig.getMaxConnectionsPerRoute(), configuration.getMaxConnectionsPerRoute());
        Assert.assertEquals(clonedConfig.getRemoteConfigurationUrl(), configuration.getRemoteConfigurationUrl());
    }

    private boolean compareCollections(Collection a, Collection b) {
        for (Object obj : a) {
            if (!b.contains(obj)) {
                return false;
            }
        }
        return a.size() == b.size();
    }

    @Test
    public void testPxVerify_notVerified() throws Exception {
        PXClient client = TestObjectUtils.blockingPXClient(configuration.getBlockingScore());
        PerimeterX perimeterx = TestObjectUtils.testablePerimeterXObject(configuration, client);
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        perimeterx.pxVerify(request, new HttpServletResponseWrapper(response));
        Assert.assertEquals(response.getStatus(), 403);
    }

    @Test
    public void testPxVerify_verified() throws Exception {
        PXClient client = TestObjectUtils.nonBlockingPXClient(configuration.getBlockingScore());
        PerimeterX perimeterx = TestObjectUtils.testablePerimeterXObject(configuration, client);
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        perimeterx.pxVerify(request, new HttpServletResponseWrapper(response));
        Assert.assertNotEquals(response.getStatus(), 403);
    }

    @Test
    public void testPXConfigURL_verified() throws Exception {
        String appId = "nitzan";
        PXConfiguration pxConfiguration = PXConfiguration.builder()
                .cookieKey("cookieToken")
                .authToken("authToken")
                .appId(appId)
                .build();

        Assert.assertEquals(pxConfiguration.getServerURL(), "https://sapi-" + appId.toLowerCase() + ".perimeterx.net");
    }

    @Test
    public void testAdvancedBlockingResponse() throws Exception {
        PXClient client = TestObjectUtils.blockingPXClient(configuration.getBlockingScore());
        PerimeterX perimeterx = TestObjectUtils.testablePerimeterXObject(configuration, client);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("accept", "application/json");
        MockHttpServletResponse response = new MockHttpServletResponse();
        perimeterx.pxVerify(request, new HttpServletResponseWrapper(response));
        Assert.assertTrue(JSONUtilsTest.isJSONValid(response.getContentAsString()));
    }

    @Test
    public void testPxVerifyCustomVerificationHandler() throws Exception {
        PXClient client = TestObjectUtils.blockingPXClient(configuration.getBlockingScore());
        PerimeterX perimeterx = TestObjectUtils.testablePerimeterXObject(configuration, client);
        perimeterx.setVerificationHandler(new UnitTestVerificationHandler());
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        perimeterx.pxVerify(request, new HttpServletResponseWrapper(response));
        Assert.assertEquals(response.getStatus(), 200);
        Assert.assertEquals((((MockHttpServletResponse) response).getContentAsString()), "custom verification handle");
    }

    @Test
    public void testPxPostVerify() throws Exception {
        PXClient client = TestObjectUtils.nonBlockingPXClient(configuration.getBlockingScore());
        PerimeterX perimeterx = TestObjectUtils.testablePerimeterXObject(configuration, client);
        perimeterx.setVerificationHandler(new UnitTestVerificationHandler());

        HttpServletRequest request = spy(new MockHttpServletRequest());
        doReturn("logger_token_123").when(request).getHeader(LOGGER_TOKEN_HEADER_NAME);

        Vector<String> headerNames = new Vector<>();
        headerNames.add(LOGGER_TOKEN_HEADER_NAME);
        Enumeration<String> headerNamesEnum = headerNames.elements();
        doReturn(headerNamesEnum).when(request).getHeaderNames();

        HttpServletResponse response = new MockHttpServletResponse();

        LoggerFactory mockLoggerFactory = mock(LoggerFactory.class);
        ConsoleLogger mockLogger = mock(ConsoleLogger.class);
        Mockito.when(mockLoggerFactory.getRequestContextLogger()).thenReturn(mockLogger);

        PXContext ctx = perimeterx.pxVerify(request, new HttpServletResponseWrapper(response));
        Assert.assertEquals(response.getStatus(), 200);
        ResponseWrapper responseWrapper = new ResponseWrapper((HttpServletResponse) response);

        Assert.assertNotNull(ctx.logger);

        ctx.logger = mock(ConsoleLogger.class);
        perimeterx.pxPostVerify(responseWrapper,ctx);
        verify(ctx.logger,times(1)).sendMemoryLogs(any(PXConfiguration.class),any(PXContext.class));


//        PXClientMock clientMock = new PXClientMock();
//        PXConfiguration configMock = mock(PXConfiguration.class);
//        Mockito.when(configMock.getPxClientInstance()).thenReturn(clientMock);
//        ctx.setPxConfiguration(configMock);
//        perimeterx.pxPostVerify(responseWrapper,ctx);
//        verify(clientMock, times(1)).sendLogs(any(String.class),any(PXContext.class));
    }

}
