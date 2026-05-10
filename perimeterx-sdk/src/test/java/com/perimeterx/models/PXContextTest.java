package com.perimeterx.models;

import com.perimeterx.api.TestCustomParamProvider;
import com.perimeterx.api.providers.DefaultHostnameProvider;
import com.perimeterx.api.providers.HostnameProvider;
import com.perimeterx.api.providers.IPProvider;
import com.perimeterx.api.providers.RemoteAddressIPProvider;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.risk.CustomParameters;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;

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
}
