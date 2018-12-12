package com.perimeterx.http;

import com.perimeterx.models.configuration.ModuleMode;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.configuration.PXDynamicConfiguration;
import junit.framework.Assert;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import testutils.PXClientMock;
import testutils.TestObjectUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;

import static org.mockito.Mockito.*;

public class PXHttpClientTest {

    PXClient pxClient;
    CloseableHttpClient httpClient;
    CloseableHttpAsyncClient asyncClient;
    PXConfiguration pxConfig;

    @BeforeMethod
    public void setup() {
        pxConfig = TestObjectUtils.generateConfiguration();
        httpClient = mock(CloseableHttpClient.class);
        asyncClient = mock(CloseableHttpAsyncClient.class);
    }

    @Test
    public void testGetRemoteConfigurations() throws IOException {
        mockValidRequest();
        pxClient = new PXClientMock(80, 0);
        PXDynamicConfiguration config = pxClient.getConfigurationFromServer();
        Assert.assertEquals("stub_app_id", config.getAppId());
        Assert.assertEquals("stub_checksum", config.getChecksum());
        Assert.assertEquals("stub_cookie_key", config.getCookieSecret());
        Assert.assertEquals(1000, config.getBlockingScore());
        Assert.assertEquals(1500, config.getApiConnectTimeout());
        Assert.assertEquals(1500, config.getS2sTimeout());
        Assert.assertEquals(config.getSensitiveHeaders(), new HashSet<String>());
        Assert.assertFalse(config.isModuleEnabled());
        Assert.assertEquals(config.getModuleMode(), ModuleMode.BLOCKING);
    }

    private void mockValidRequest() {
        try {
            String json = "{\"moduleEnabled\":false,\"cookieKey\":\"a_cookie_key\",\"blockingScore\":1000,\"appId\":\"a_app_id\",\"moduleMode\":\"blocking\",\"sensitiveHeaders\":[],\"connectTimeout\":3000,\"riskTimeout\":3000,\"debugMode\":false,\"checksum\":\"a_check_sum\"}";
            HttpEntity entity = mock(HttpEntity.class);
            CloseableHttpResponse response = mock(CloseableHttpResponse.class);
            StatusLine statusLine = mock(StatusLine.class);
            when(statusLine.getStatusCode()).thenReturn(200);
            when(entity.getContent()).thenReturn(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)));
            when(response.getEntity()).thenReturn(entity);
            when(httpClient.execute(isA(HttpUriRequest.class))).thenReturn(response);
            when(response.getStatusLine()).thenReturn(statusLine);
        } catch (Exception e) {
            Assert.fail();
        }
    }
}
