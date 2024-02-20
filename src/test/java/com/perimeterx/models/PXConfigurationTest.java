package com.perimeterx.models;

import com.perimeterx.models.configuration.ModuleMode;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.utils.FilesUtils;
import org.easymock.EasyMock;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashSet;

@PrepareForTest(FilesUtils.class)
public class PXConfigurationTest extends PowerMockTestCase {
    @Test
    public void testMergeConfigurations() throws FileNotFoundException {
        PXConfiguration pxConfiguration = PXConfiguration.builder()
                .appId("appId")
                .cookieKey("cookieKey")
                .authToken("authToken")
                .sensitiveRoutes(new HashSet<>(Arrays.asList("/profile")))
                .moduleMode(ModuleMode.BLOCKING)
                .remoteConfigurationEnabled(false)
                .validateRequestQueueInterval(1000 * 100)
                .bypassMonitorHeader("X-PX-BYPASS-MONITOR")
                .configFilePath("config.json")
                .build();
        PowerMock.mockStaticPartial(FilesUtils.class, "readFile");
        EasyMock.expect(FilesUtils.readFile("config.json")).andReturn("{\n" +
                "  \"px_module_mode\": 0,\n" +
                "  \"px_remote_configuration_interval_ms\": 1\n" +
                "}\n").times(2);
        PowerMock.replayAll();
        pxConfiguration.mergeConfigurations();
        Assert.assertEquals(pxConfiguration.getModuleMode(), ModuleMode.MONITOR);
        Assert.assertEquals(pxConfiguration.getRemoteConfigurationInterval(), 1);
    }

    @Test
    public void testSensitiveHeadersDefaultValue() {
        PXConfiguration configuration = PXConfiguration.builder()
                .appId("appId")
                .cookieKey("cookieKey")
                .authToken("authToken")
                .build();

        Assert.assertEquals(configuration.getSensitiveHeaders(), new HashSet<>(Arrays.asList("cookieorig", "cookies")));
    }
    @Test
    public void testSensitiveHeadersNonDefaultValues() {
        PXConfiguration configuration = PXConfiguration.builder()
                .appId("appId")
                .cookieKey("cookieKey")
                .authToken("authToken")
                .sensitiveHeaders(new HashSet<>(Arrays.asList("a","b","c")))
                .build();

        Assert.assertEquals(configuration.getSensitiveHeaders(), new HashSet<>(Arrays.asList("a","b","c")));
    }
    @Test
    public void testSensitiveHeadersNonDefaultWithUpperCaseValues() {
        PXConfiguration configuration = PXConfiguration.builder()
                .appId("appId")
                .cookieKey("cookieKey")
                .authToken("authToken")
                .sensitiveHeaders(new HashSet<>(Arrays.asList("a","B","c")))
                .build();

        Assert.assertEquals(configuration.getSensitiveHeaders(), new HashSet<>(Arrays.asList("a","b","c")));
    }
    @Test
    public void testSensitiveHeadersNullValue() {
        PXConfiguration configuration = PXConfiguration.builder()
                .appId("appId")
                .cookieKey("cookieKey")
                .authToken("authToken")
                .sensitiveHeaders(null)
                .build();

        Assert.assertEquals(configuration.getSensitiveHeaders(), new HashSet<>());
    }
}
