package com.perimeterx.api;

import com.perimeterx.http.PXClient;
import com.perimeterx.models.configuration.PXConfiguration;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.testng.Assert;
import org.testng.annotations.Test;
import testutils.ConfiguredTest;
import testutils.TestObjectUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.util.Collection;

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
        Assert.assertEquals(clonedConfig.shouldSendPageActivities(), configuration.shouldSendPageActivities());
        Assert.assertEquals(clonedConfig.wasSignedWithIP(), configuration.wasSignedWithIP());
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
        PXConfiguration pxConfiguration = new PXConfiguration.Builder()
                .cookieKey("cookieToken")
                .authToken("authToken")
                .appId(appId)
                .build();

        Assert.assertEquals(pxConfiguration.getServerURL(), "https://sapi-" + appId.toLowerCase() + ".perimeterx.net");
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

}
