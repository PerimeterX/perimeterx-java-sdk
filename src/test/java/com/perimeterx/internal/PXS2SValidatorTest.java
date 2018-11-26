package com.perimeterx.internal;

import com.perimeterx.api.TestCustomParamProvider;
import com.perimeterx.api.providers.DefaultHostnameProvider;
import com.perimeterx.api.providers.HostnameProvider;
import com.perimeterx.api.providers.IPProvider;
import com.perimeterx.api.providers.RemoteAddressIPProvider;
import com.perimeterx.http.PXClient;
import com.perimeterx.internals.PXS2SValidator;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.exceptions.PXException;
import com.perimeterx.models.httpmodels.RiskRequest;
import com.perimeterx.models.risk.BlockReason;
import com.perimeterx.models.risk.CustomParameters;
import com.perimeterx.models.risk.S2SCallReason;
import com.perimeterx.utils.BlockAction;
import com.perimeterx.utils.Constants;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import testutils.PXClientMock;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.testng.Assert.assertEquals;

/**
 * Test {@link PXS2SValidator}
 * <p>
 * Created by shikloshi on 16/07/2016.
 */
@Test
public class PXS2SValidatorTest {

    private HttpServletRequest request;

    private PXConfiguration pxConfig;
    private PXContext context;
    private IPProvider ipProvider;
    private HostnameProvider hostnameProvider;
    private PXClient client;
    private String dataEnrichmentString = "{\"cookieMonster\":\"ilai\"}";

    private PXS2SValidator validator;

    @BeforeMethod
    public void setUp() {
        this.pxConfig = new PXConfiguration.Builder()
                .appId("APP_ID")
                .authToken("AUTH_123")
                .cookieKey("COOKIE_123")
                .build();
        this.client = new PXClientMock(50, Constants.CAPTCHA_SUCCESS_CODE);
        this.request = new MockHttpServletRequest();
        this.ipProvider = new RemoteAddressIPProvider();
        this.hostnameProvider = new DefaultHostnameProvider();
        this.context = new PXContext(request, this.ipProvider, this.hostnameProvider, pxConfig);
        this.validator = new PXS2SValidator(this.client, pxConfig);
    }

    @Test
    public void verifyTest() throws PXException {
        validator.verify(context);
        Assert.assertEquals(context.getRiskScore(), 50);
        assertEquals(context.getDataEnrichment().toString(), dataEnrichmentString);
    }

    @Test
    public void jsChallengeTest() throws PXException {
        this.client = new PXClientMock(100, Constants.CAPTCHA_SUCCESS_CODE, true);
        this.validator = new PXS2SValidator(this.client, this.pxConfig);
        context.setS2sCallReason(S2SCallReason.SENSITIVE_ROUTE.getValue());
        validator.verify(context);
        Assert.assertEquals(BlockAction.CHALLENGE, context.getBlockAction());
        Assert.assertEquals("<html><body></body></html>", context.getBlockActionData());
        Assert.assertEquals(BlockReason.CHALLENGE, context.getBlockReason());
    }

    @Test
    public void customParamsTest() throws PXException, IOException {
        CustomParameters customParameters = new CustomParameters();
        customParameters.setCustomParam2("number 2");
        customParameters.setCustomParam1("number 1");
        customParameters.setCustomParam10("number 10");
        TestCustomParamProvider testCustomParamProvider = Mockito.spy(new TestCustomParamProvider(customParameters));

        PXConfiguration conf = new PXConfiguration.Builder()
                .appId("APP_ID")
                .authToken("AUTH_123")
                .cookieKey("COOKIE_123")
                .customParametersProvider(testCustomParamProvider)
                .build();
        this.client = Mockito.spy(new PXClientMock(0, Constants.CAPTCHA_SUCCESS_CODE, true));
        this.validator = new PXS2SValidator(this.client, conf);
        validator.verify(context);
        Mockito.verify(testCustomParamProvider, times(1)).buildCustomParameters(any(PXConfiguration.class), any(PXContext.class));
        Mockito.verify(client, times(1)).riskApiCall(any(RiskRequest.class));
    }
}
