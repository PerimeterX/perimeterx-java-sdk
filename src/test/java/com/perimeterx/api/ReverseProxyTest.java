package com.perimeterx.api;

import com.perimeterx.api.providers.CombinedIPProvider;
import com.perimeterx.api.proxy.DefaultReverseProxy;
import com.perimeterx.api.proxy.ReverseProxy;
import com.perimeterx.http.PXClient;
import com.perimeterx.models.configuration.PXConfiguration;
import org.apache.http.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.junit.Assert;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import testutils.TestObjectUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

@Test
public class ReverseProxyTest {

    PXConfiguration pxConfiguration;

    private CloseableHttpResponse getMockedHttpResponse(String responseBody, String contentType) throws IOException {
        HttpEntity entity = new BasicHttpEntity();
        ((BasicHttpEntity) entity).setContent(new ByteArrayInputStream(responseBody.getBytes()));

        List<Header> responseHeaders = new ArrayList<Header>(Arrays.asList(
                new BasicHeader("Cache-Control", "max-age=600"),
                new BasicHeader("Content-Type:", contentType)
        ));

        if (responseBody != null) {
            responseHeaders.add(new BasicHeader("Content-Length", String.valueOf(responseBody.length())));
        }

        CloseableHttpResponse httpResponse = mock(CloseableHttpResponse.class);
        StatusLine statusLine = mock(StatusLine.class);

        //and:
        when(statusLine.getStatusCode()).thenReturn(200);
        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(httpResponse.getAllHeaders()).thenReturn(responseHeaders.toArray(new Header[]{}));
        when(httpResponse.getEntity()).thenReturn(entity);

        return httpResponse;
    }

    @BeforeMethod
    public void setUp() {
        pxConfiguration = PXConfiguration.builder()
                .appId("PX12345678")
                .cookieKey("COOKIE_KEY")
                .authToken("AUTH_TOKEN")
                .firstPartyEnabled(false)
                .build();
    }

    @Test
    public void testPerimeterXReverseProxyMatchClient() throws Exception {
        PXClient client = TestObjectUtils.blockingPXClient(pxConfiguration.getBlockingScore());
        PerimeterX perimeterx = TestObjectUtils.testablePerimeterXObject(pxConfiguration, client);
        HttpServletRequest request = new MockHttpServletRequest();
        ((MockHttpServletRequest) request).setRequestURI("/12345678/init.js");
        HttpServletResponse response = new MockHttpServletResponse();

        perimeterx.pxVerify(request, new HttpServletResponseWrapper(response));
        Assert.assertEquals("", ((MockHttpServletResponse) response).getContentAsString());
        Assert.assertEquals("application/javascript", response.getHeader("Content-Type"));
    }

    @Test
    public void testPerimeterXReverseProxyMatchXhr() throws Exception {
        PXClient client = TestObjectUtils.blockingPXClient(pxConfiguration.getBlockingScore());
        PerimeterX perimeterx = TestObjectUtils.testablePerimeterXObject(pxConfiguration, client);
        HttpServletRequest request = new MockHttpServletRequest();
        ((MockHttpServletRequest) request).setRequestURI("/12345678/xhr/api/v1/collector");
        HttpServletResponse response = new MockHttpServletResponse();

        perimeterx.pxVerify(request, new HttpServletResponseWrapper(response));
        Assert.assertEquals("{}", ((MockHttpServletResponse) response).getContentAsString());
        Assert.assertEquals("application/json", response.getHeader("Content-Type"));
    }

    @Test
    public void testReverseProxyClientMethod() throws Exception {
        pxConfiguration = PXConfiguration.builder()
                .appId("PX12345678")
                .cookieKey("COOKIE_KEY")
                .authToken("AUTH_TOKEN")
                .firstPartyEnabled(true)
                .build();

        CloseableHttpClient mockProxyHttpClient = mock(CloseableHttpClient.class);
        CloseableHttpResponse mockedHttpResponse = getMockedHttpResponse("function()", "application/javascript");
        when(mockProxyHttpClient.execute(any(HttpHost.class), any(HttpRequest.class))).thenReturn(mockedHttpResponse);

        ReverseProxy reverseProxy = new DefaultReverseProxy(pxConfiguration, new CombinedIPProvider(pxConfiguration));
        ((DefaultReverseProxy) reverseProxy).setProxyClient(mockProxyHttpClient);

        HttpServletRequest request = new MockHttpServletRequest();
        ((MockHttpServletRequest) request).setRequestURI("/12345678/init.js");
        HttpServletResponse response = new MockHttpServletResponse();

        reverseProxy.reversePxClient(request, response);
        verify(mockProxyHttpClient, times(1)).execute(any(HttpHost.class), any(HttpRequest.class));
        Assert.assertEquals("function()", ((MockHttpServletResponse) response).getContentAsString());
        Assert.assertEquals(response.getHeaderNames().size(), mockedHttpResponse.getAllHeaders().length);
    }

    @Test
    public void testReverseProxyXHRMethod() throws Exception {
        pxConfiguration = PXConfiguration.builder()
                .appId("PX12345678")
                .cookieKey("COOKIE_KEY")
                .authToken("AUTH_TOKEN")
                .firstPartyEnabled(true)
                .build();

        CloseableHttpClient mockProxyHttpClient = mock(CloseableHttpClient.class);
        CloseableHttpResponse mockedHttpResponse = getMockedHttpResponse("{\"some\": '\"answer\"}", "application/json");
        when(mockProxyHttpClient.execute(any(HttpHost.class), any(HttpRequest.class))).thenReturn(mockedHttpResponse);

        ReverseProxy reverseProxy = new DefaultReverseProxy(pxConfiguration, new CombinedIPProvider(pxConfiguration));
        ((DefaultReverseProxy) reverseProxy).setProxyClient(mockProxyHttpClient);

        HttpServletRequest request = new MockHttpServletRequest();
        ((MockHttpServletRequest) request).setRequestURI("/12345678/xhr/api/v1/collector");
        HttpServletResponse response = new MockHttpServletResponse();

        reverseProxy.reversePxXhr(request, response);
        verify(mockProxyHttpClient, times(1)).execute(any(HttpHost.class), any(HttpRequest.class));
        Assert.assertEquals("{\"some\": '\"answer\"}", ((MockHttpServletResponse) response).getContentAsString());
        Assert.assertEquals(response.getHeaderNames().size(), mockedHttpResponse.getAllHeaders().length);
    }

}
