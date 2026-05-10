package com.perimeterx.models;

import com.perimeterx.api.TestCustomParamProvider;
import com.perimeterx.api.providers.DefaultHostnameProvider;
import com.perimeterx.api.providers.HostnameProvider;
import com.perimeterx.api.providers.IPProvider;
import com.perimeterx.api.providers.RemoteAddressIPProvider;
import com.perimeterx.http.RequestWrapper;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.risk.CustomParameters;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

/**
 * Test {@link PXContext}
 */
@Test
public class PXContextTest {
    private HttpServletRequest request;
    private IPProvider ipProvider;
    private HostnameProvider hostnameProvider;

    @BeforeMethod
    public void setUp() {
        this.request = new MockHttpServletRequest();
        this.ipProvider = new RemoteAddressIPProvider();
        this.hostnameProvider = new DefaultHostnameProvider();
    }

    @Test
    public void customParamsTest() {
        CustomParameters customParameters = new CustomParameters();
        customParameters.setCustomParam1("number1");
        customParameters.setCustomParam2("number2");
        customParameters.setCustomParam10("number10");
        TestCustomParamProvider spyTestCustomParamProvider = Mockito.spy(new TestCustomParamProvider(customParameters));

        PXConfiguration pxConfig = PXConfiguration.builder()
                .appId("APP_ID")
                .authToken("AUTH_123")
                .cookieKey("COOKIE_123")
                .customParametersProvider(spyTestCustomParamProvider)
                .build();

        PXContext context = new PXContext(request, this.ipProvider, this.hostnameProvider, pxConfig);
        Assert.assertEquals(context.getCustomParameters().getCustomParam1(), "number1");
        Assert.assertEquals(context.getCustomParameters().getCustomParam2(), "number2");
        Assert.assertEquals(context.getCustomParameters().getCustomParam10(), "number10");

        Mockito.verify(spyTestCustomParamProvider).buildCustomParameters(pxConfig, context);
    }

    @Test
    public void allRequestHeadersShouldBeInPXContext() {
        CustomParameters customParameters = new CustomParameters();
        customParameters.setCustomParam1("number1");
        TestCustomParamProvider spyTestCustomParamProvider = Mockito.spy(new TestCustomParamProvider(customParameters));
        PXConfiguration pxConfig = PXConfiguration.builder()
                .appId("APP_ID")
                .authToken("AUTH_123")
                .cookieKey("COOKIE_123")
                .customParametersProvider(spyTestCustomParamProvider)
                .build();
        ((MockHttpServletRequest) request).addHeader("TEST-BYPASS", "0");
        PXContext context = new PXContext(request, this.ipProvider, this.hostnameProvider, pxConfig);
        Assert.assertEquals(context.getHeaders().size(), Collections.list(request.getHeaderNames()).size());
    }

    @Test
    public void allRequestWrapperHeadersShouldBeInPXContext() {
        CustomParameters customParameters = new CustomParameters();
        customParameters.setCustomParam1("number1");
        TestCustomParamProvider spyTestCustomParamProvider = Mockito.spy(new TestCustomParamProvider(customParameters));
        PXConfiguration pxConfig = PXConfiguration.builder()
                .appId("APP_ID")
                .authToken("AUTH_123")
                .cookieKey("COOKIE_123")
                .customParametersProvider(spyTestCustomParamProvider)
                .build();
        ((MockHttpServletRequest) request).addHeader("TEST-BYPASS", "0");
        RequestWrapper requestWrapper = new RequestWrapper(request);
        requestWrapper.addHeader("client-ip", "127.0.0.1");
        requestWrapper.addHeader("accept", "application/json");
        requestWrapper.addHeader("content-type", "application/json");

        PXContext context = new PXContext(requestWrapper, this.ipProvider, this.hostnameProvider, pxConfig);
        Assert.assertEquals(context.getHeaders().size(), Collections.list(request.getHeaderNames()).size() + 3);
    }
}
