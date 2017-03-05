package com.perimeterx.http;

import com.perimeterx.api.PXConfiguration;
import com.perimeterx.api.activities.BufferedActivityHandler;
import com.perimeterx.api.providers.DefaultHostnameProvider;
import com.perimeterx.api.providers.HostnameProvider;
import com.perimeterx.api.providers.IPProvider;
import com.perimeterx.api.providers.RemoteAddressIPProvider;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.activities.Activity;
import com.perimeterx.models.activities.ActivityFactory;
import com.perimeterx.models.exceptions.PXException;
import com.perimeterx.utils.Constants;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.protocol.BasicAsyncRequestProducer;
import org.apache.http.nio.protocol.HttpAsyncResponseConsumer;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.testng.annotations.BeforeMethod;
import testutils.TestObjectUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Future;

import static org.mockito.Mockito.*;

/**
 * Created by nitzangoldfeder on 23/02/2017.
 */
@org.testng.annotations.Test
public class PXHttpClientTest {

    private BufferedActivityHandler bufferedActivityHandler;
    private PXHttpClient pxClient;
    private PXConfiguration configuration;
    private HttpServletRequest request;
    private IPProvider ipProvider;
    private HostnameProvider hostnameProvider;
    private PXContext context;


    @BeforeMethod
    public void setUp() {
        this.pxClient = mock(PXHttpClient.class);
        this.configuration = TestObjectUtils.generateConfiguration();
        this.bufferedActivityHandler = new BufferedActivityHandler(pxClient,configuration);

        this.request = new MockHttpServletRequest();
        this.ipProvider = new RemoteAddressIPProvider();
        this.hostnameProvider = new DefaultHostnameProvider();
        this.context = new PXContext(request, this.ipProvider, this.hostnameProvider, "appId");

    }

    @Test
    public void testSendActivityOnMaxBuffer() throws IOException, PXException {
        for (int i = 0; i <= this.configuration.getMaxBufferLen(); i++) {
            bufferedActivityHandler.handleBlockActivity(context);
        }

        verify(pxClient,atLeastOnce()).sendBatchActivities(any(List.class));
    }

    @Test
    public void testDontSendActivityBelowMaxBuffer() throws IOException, PXException {
        for (int i = 0; i < this.configuration.getMaxBufferLen() - 1; i++) {
            bufferedActivityHandler.handleBlockActivity(context);
        }
        verify(pxClient,never()).sendBatchActivities(any(List.class));

    }
}
