package com.perimeterx.api;

import com.perimeterx.api.activities.ActivityHandler;
import com.perimeterx.api.activities.DefaultActivityHandler;
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
import java.util.Collections;
import java.util.HashSet;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Test
public class RemoteConfigurationsTest {
    PXConfiguration config;
    PXHttpClient pxClient;
    ActivityHandler activityHandler;

    @BeforeMethod
    public void setUp() {
        config = TestObjectUtils.generateConfiguration();
        pxClient = mock(PXHttpClient.class);
        activityHandler = mock(DefaultActivityHandler.class);
    }

    @Test
    public void pullConfigurationsSuccess() throws IOException {
        PXDynamicConfiguration pxDynamicConfiguration = getDynamicConfiguration("stub_app_id", "stub_checksum",
                1000, "stub_cookie_key", 1500, 1500, new HashSet<String>(), false, ModuleMode.BLOCKING);
        when(pxClient.getConfigurationFromServer()).thenReturn(pxDynamicConfiguration);
        RemoteConfigurationManager remoteConfigurationManager = new DefaultRemoteConfigManager(config, pxClient);
        TimerConfigUpdater timerConfigUpdater = new TimerConfigUpdater(remoteConfigurationManager, config, activityHandler);
        timerConfigUpdater.run();
        Assert.assertEquals("stub_app_id", config.getAppId());
        Assert.assertEquals("stub_cookie_key", config.getCookieKeys().get(0));
        Assert.assertEquals("stub_checksum", config.getChecksum());
        Assert.assertEquals(1000, config.getBlockingScore());
        Assert.assertEquals(1500, config.getConnectionTimeout());
        Assert.assertEquals(1500, config.getApiTimeout());
        Assert.assertEquals(config.getSensitiveHeaders(), new HashSet<String>());
        Assert.assertEquals(false, config.isModuleEnabled());
        Assert.assertEquals(config.getModuleMode(), ModuleMode.BLOCKING);
    }

    @Test
    public void pullConfigurationsFailed() throws IOException {
        PXDynamicConfiguration pxDynamicConfiguration = getDynamicConfiguration("stub_app_id", "stub_checksum",
                1000, "stub_cookie_key", 1500, 1500, new HashSet<String>(), true, ModuleMode.BLOCKING);
        when(pxClient.getConfigurationFromServer()).thenReturn(pxDynamicConfiguration);
        RemoteConfigurationManager remoteConfigurationManager = new DefaultRemoteConfigManager(config, pxClient);
        TimerConfigUpdater timerConfigUpdater = new TimerConfigUpdater(remoteConfigurationManager, config, activityHandler);
        timerConfigUpdater.run();
        when(pxClient.getConfigurationFromServer()).thenReturn(null);
        timerConfigUpdater.run();
        Assert.assertTrue(config.isModuleEnabled() == true);
    }

    private PXDynamicConfiguration getDynamicConfiguration(String appId, String checksum, int blockingScore, String cookieSecert,
                                                           int s2sTimeout, int connectionTimeout, HashSet<String> sensitiveRotues, boolean moduleEnabled, ModuleMode moduleMode) {
        PXDynamicConfiguration pxDynamicConfig = new PXDynamicConfiguration();
        pxDynamicConfig.setAppId(appId);
        pxDynamicConfig.setChecksum(checksum);
        pxDynamicConfig.setBlockingScore(blockingScore);
        pxDynamicConfig.setCookieSecrets(Collections.singletonList(cookieSecert));
        pxDynamicConfig.setS2sTimeout(s2sTimeout);
        pxDynamicConfig.setApiConnectTimeout(connectionTimeout);
        pxDynamicConfig.setSensitiveHeaders(sensitiveRotues);
        pxDynamicConfig.setModuleEnabled(moduleEnabled);
        pxDynamicConfig.setModuleMode(moduleMode);

        return pxDynamicConfig;
    }
}
