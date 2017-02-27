package com.perimeterx.http;

import com.perimeterx.api.PXConfiguration;
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

import static org.mockito.Mockito.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.concurrent.Future;

/**
 * Created by nitzangoldfeder on 23/02/2017.
 */
@org.testng.annotations.Test
public class PXHttpClientTest {

    private PXHttpClient pxClient;
    private PXConfiguration configuration;
    private HttpServletRequest request;
    private IPProvider ipProvider;
    private HostnameProvider hostnameProvider;
    private CloseableHttpAsyncClient mockAsyncHttpClient;


    @BeforeMethod
    public void setUp() {

        configuration = TestObjectUtils.generateConfiguration();
        mockAsyncHttpClient = spy(HttpAsyncClients.createDefault());

        doReturn(mock(Future.class)).when(mockAsyncHttpClient).execute(any(BasicAsyncRequestProducer.class),any(HttpAsyncResponseConsumer.class),any(FutureCallback.class));

        pxClient = PXHttpClient.getInstance(this.configuration,mockAsyncHttpClient,null);
        this.request = new MockHttpServletRequest();
        this.ipProvider = new RemoteAddressIPProvider();
        this.hostnameProvider = new DefaultHostnameProvider();
    }

    @Test
    public void testSendActivityOnMaxBuffer() throws IOException, PXException {
        try{
            PXContext context = new PXContext(request, this.ipProvider, this.hostnameProvider, "appId");
            for (int i = 0; i <= this.configuration.getMaxBufferLen(); i++){
                Activity activity = ActivityFactory.createActivity(Constants.ACTIVITY_BLOCKED, configuration.getAppId(), context);
                pxClient.sendActivity(activity);
            }

            Assert.assertEquals(0,pxClient.getActivitiesBuffer().size());
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    @Test
    public void testDontSendActivityBelowMaxBuffer() throws IOException, PXException {

        PXContext context = new PXContext(request, this.ipProvider, this.hostnameProvider, "appId");

        for (int i = 0; i < this.configuration.getMaxBufferLen() - 1; i++){
            Activity activity = ActivityFactory.createActivity(Constants.ACTIVITY_BLOCKED, configuration.getAppId(), context);
            pxClient.sendActivity(activity);
            Assert.assertEquals(i+1,pxClient.getActivitiesBuffer().size());
        }

        verify(mockAsyncHttpClient,never()).execute(any(BasicAsyncRequestProducer.class),any(HttpAsyncResponseConsumer.class),any(FutureCallback.class));
        Assert.assertEquals(this.configuration.getMaxBufferLen()-1,pxClient.getActivitiesBuffer().size());
    }
}
