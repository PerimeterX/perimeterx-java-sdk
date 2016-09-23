package com.perimeterx.internal;

import com.perimeterx.http.PXClient;
import com.perimeterx.internals.PXS2SValidator;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.exceptions.PXException;
import com.perimeterx.models.httpmodels.RiskRequest;
import com.perimeterx.models.httpmodels.RiskResponse;
import com.perimeterx.utils.Constants;
import org.springframework.mock.web.MockHttpServletRequest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import testutils.PXClientMock;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Test {@link PXS2SValidator}
 * <p>
 * Created by shikloshi on 16/07/2016.
 */
@Test
public class PXS2SValidatorTest {

    private HttpServletRequest request;

    private PXContext context;
    private PXClient client;

    private PXS2SValidator validator;
    private RiskRequest riskRequest;

    @BeforeMethod
    public void setUp() throws Exception {
        this.client = new PXClientMock(50, Constants.CAPTCHA_SUCCESS_CODE);
        this.request = new MockHttpServletRequest();
        this.context = new PXContext(request);
        this.riskRequest = RiskRequest.fromContext(context);
        validator = new PXS2SValidator(this.client);
    }

    @Test
    public void verifyTest() throws PXException, IOException {
        RiskResponse verify = validator.verify(this.riskRequest);
        Assert.assertEquals(verify.getScores().getNonHuman(), 50);
    }
}
