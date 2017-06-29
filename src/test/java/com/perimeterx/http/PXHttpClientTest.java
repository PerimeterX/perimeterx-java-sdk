package com.perimeterx.http;

import com.perimeterx.models.configuration.ModuleMode;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.configuration.PXConfigurationStub;
import junit.framework.Assert;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import testutils.TestObjectUtils;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;

import static org.mockito.Mockito.*;

@Test
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
    public void testGetRemoteConfigurations(){
        mockValidRequest();
        pxClient = PXHttpClient.getInstance(pxConfig, asyncClient, httpClient);
        PXConfigurationStub config = pxClient.getConfigurationFromServer();
        Assert.assertTrue(config.getAppId().equals("a_app_id"));
        Assert.assertTrue(config.getChecksum().equals("a_check_sum"));
        Assert.assertTrue(config.getCookieSecret().equals("a_cookie_key"));
        Assert.assertTrue(config.getBlockingScore() == 1000);
        Assert.assertTrue(config.getApiConnectTimeout() == 3000);
        Assert.assertTrue(config.getS2sTimeout() == 3000);
        Assert.assertTrue(config.getSensitiveHeaders().equals(new HashSet<String>()));
        Assert.assertTrue(config.isModuleEnabled() == false);
        Assert.assertTrue(config.getModuleMode().equals(ModuleMode.BLOCKING));
    }

    private void mockValidRequest() {
        try{
            String json = "{\"moduleEnabled\":false,\"cookieKey\":\"a_cookie_key\",\"blockingScore\":1000,\"appId\":\"a_app_id\",\"moduleMode\":\"blocking\",\"sensitiveHeaders\":[],\"connectTimeout\":3000,\"riskTimeout\":3000,\"debugMode\":false,\"checksum\":\"a_check_sum\"}";
            HttpEntity entity = mock(HttpEntity.class);
            CloseableHttpResponse response = mock(CloseableHttpResponse.class);
            StatusLine statusLine = mock(StatusLine.class);
            when(statusLine.getStatusCode()).thenReturn(200);
            when(entity.getContent()).thenReturn(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)));
            when(response.getEntity()).thenReturn(entity);
            when(httpClient.execute(isA(HttpUriRequest.class))).thenReturn(response);
            when(response.getStatusLine()).thenReturn(statusLine);
        }catch (Exception e){
            Assert.assertTrue(false);
        }
    }
}
