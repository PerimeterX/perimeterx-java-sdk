package com.perimeterx.api;

import com.perimeterx.api.remoteconfigurations.DefaultRemoteConfigurationManager;
import com.perimeterx.api.remoteconfigurations.RemoteConfigurationManager;
import com.perimeterx.http.PXClient;
import com.perimeterx.models.configuration.ModuleMode;
import com.perimeterx.models.configuration.PXConfiguration;
import junit.framework.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import testutils.PXClientMock;
import testutils.TestObjectUtils;

import java.util.HashSet;


@Test
public class RemoteConfigurationsTest {
    PXConfiguration config;
    PXClient pxClient;

    @BeforeMethod
    public void setUp() {
        config = TestObjectUtils.generateConfiguration();
        pxClient = new PXClientMock(100, 403);
    }

    @Test
    public void pullConfigurationsSuccess(){
        RemoteConfigurationManager remoteConfigurationManager = new DefaultRemoteConfigurationManager(config, pxClient);
        remoteConfigurationManager.getConfiguration();
        Assert.assertTrue(config.getAppId().equals("stub_app_id"));
        Assert.assertTrue(config.getCookieKey().equals("stub_cookie_key"));
        Assert.assertTrue(config.getChecksum().equals("stub_checksum"));
        Assert.assertTrue(config.getBlockingScore() == 1000);
        Assert.assertTrue(config.getConnectionTimeout() == 1500);
        Assert.assertTrue(config.getApiTimeout() == 1500);
        Assert.assertTrue(config.getSensitiveHeaders().equals(new HashSet<String>()));
        Assert.assertTrue(config.isModuleEnabled() == false);
        Assert.assertTrue(config.getModuleMode().equals(ModuleMode.BLOCKING));
    }
}
