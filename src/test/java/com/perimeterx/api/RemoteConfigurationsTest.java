package com.perimeterx.api;

import com.perimeterx.api.remoteconfigurations.DefaultRemoteConfigManager;
import com.perimeterx.api.remoteconfigurations.RemoteConfigurationManager;
import com.perimeterx.api.remoteconfigurations.TimerConfigUpdater;
import com.perimeterx.http.PXHttpClient;
import com.perimeterx.models.configuration.ModuleMode;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.configuration.PXDynamicConfiguration;
import junit.framework.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import testutils.TestObjectUtils;

import java.io.IOException;
import java.util.HashSet;

import static org.mockito.Mockito.*;


@Test
public class RemoteConfigurationsTest {
    PXConfiguration config;
    PXHttpClient pxClient;

    @BeforeMethod
    public void setUp() {
        config = TestObjectUtils.generateConfiguration();
        pxClient = mock(PXHttpClient.class);
    }

    @Test
    public void pullConfigurationsSuccess() throws IOException{
        PXDynamicConfiguration pxDynamicConfiguration = getDynamicConfiguration("stub_app_id", "stub_checksum",
                                1000, "stub_cookie_key", 1500, 1500, new HashSet<String>(), false, ModuleMode.BLOCKING);
        when(pxClient.getConfigurationFromServer()).thenReturn(pxDynamicConfiguration);
        RemoteConfigurationManager remoteConfigurationManager = new DefaultRemoteConfigManager(config, pxClient);
        TimerConfigUpdater timerConfigUpdater = new TimerConfigUpdater(remoteConfigurationManager, config, pxClient);
        timerConfigUpdater.run();
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

    @Test
    public void  pullConfigurationsFailed() throws IOException{
        PXDynamicConfiguration pxDynamicConfiguration = getDynamicConfiguration("stub_app_id", "stub_checksum",
                1000, "stub_cookie_key", 1500, 1500, new HashSet<String>(), true, ModuleMode.BLOCKING);
        when(pxClient.getConfigurationFromServer()).thenReturn(pxDynamicConfiguration);
        RemoteConfigurationManager remoteConfigurationManager = new DefaultRemoteConfigManager(config, pxClient);
        TimerConfigUpdater timerConfigUpdater = new TimerConfigUpdater(remoteConfigurationManager, config, pxClient);
        timerConfigUpdater.run();
        when(pxClient.getConfigurationFromServer()).thenReturn(null);
        timerConfigUpdater.run();
        Assert.assertTrue(config.isModuleEnabled() == true);
    }

    private PXDynamicConfiguration getDynamicConfiguration(String appId, String checksum, int blockingScore, String cookieSecert,
                                   int s2sTimeout, int connectionTimeout, HashSet<String> sensitiveRotues, boolean moduleEnabled, ModuleMode moduleMode){
        PXDynamicConfiguration pxDynamicConfig = new PXDynamicConfiguration();
        pxDynamicConfig.setAppId(appId);
        pxDynamicConfig.setChecksum(checksum);
        pxDynamicConfig.setBlockingScore(blockingScore);
        pxDynamicConfig.setCookieSecret(cookieSecert);
        pxDynamicConfig.setS2sTimeout(s2sTimeout);
        pxDynamicConfig.setApiConnectTimeout(connectionTimeout);
        pxDynamicConfig.setSensitiveHeaders(sensitiveRotues);
        pxDynamicConfig.setModuleEnabled(moduleEnabled);
        pxDynamicConfig.setModuleMode(moduleMode);

        return pxDynamicConfig;
    }
}
