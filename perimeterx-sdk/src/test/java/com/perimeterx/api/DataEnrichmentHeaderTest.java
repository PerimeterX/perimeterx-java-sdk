package com.perimeterx.api;

import com.perimeterx.http.PXClient;
import com.perimeterx.http.RequestWrapper;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.configuration.ModuleMode;
import com.perimeterx.models.configuration.PXConfiguration;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import testutils.TestObjectUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponseWrapper;

@Test
public class DataEnrichmentHeaderTest {

    private static final String DE_HEADER_NAME = "X-PX-Data-Enrichment";

    private PXConfiguration configuration;

    @BeforeMethod
    public void setup() {
        configuration = PXConfiguration.builder()
                .appId("appId")
                .authToken("token")
                .cookieKey("cookieKey")
                .moduleMode(ModuleMode.BLOCKING)
                .remoteConfigurationEnabled(false)
                .blockingScore(70)
                .pxDataEnrichmentHeaderName(DE_HEADER_NAME)
                .build();
    }

    /**
     * Simulates the customer's scenario: a custom HttpServletRequestWrapper
     * that is NOT our RequestWrapper. Before the fix, this would throw a
     * ClassCastException inside setDataEnrichmentHeader.
     */
    @Test
    public void testNonRequestWrapper_oldBehaviorWouldThrowClassCastException() {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        HttpServletRequest customWrapper = new HttpServletRequestWrapper(mockRequest);

        Assert.assertFalse(customWrapper instanceof RequestWrapper,
                "Test precondition: the custom wrapper must NOT be a RequestWrapper");

        boolean wouldThrow = false;
        try {
            ((RequestWrapper) customWrapper).addHeader(DE_HEADER_NAME, "test");
        } catch (ClassCastException e) {
            wouldThrow = true;
        }
        Assert.assertTrue(wouldThrow,
                "Direct cast to RequestWrapper should throw ClassCastException for non-RequestWrapper requests");
    }

    /**
     * With the fix: passing a non-RequestWrapper request to pxVerify should
     * NOT throw, and the DE data should be available via request.getAttribute().
     */
    @Test
    public void testNonRequestWrapper_dataEnrichmentSetAsAttribute() throws Exception {
        PXClient client = TestObjectUtils.nonBlockingPXClient(configuration.getBlockingScore());
        PerimeterX perimeterx = TestObjectUtils.testablePerimeterXObject(configuration, client);

        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        HttpServletRequest customWrapper = new HttpServletRequestWrapper(mockRequest);

        Assert.assertFalse(customWrapper instanceof RequestWrapper,
                "Test precondition: must not be a RequestWrapper");

        MockHttpServletResponse response = new MockHttpServletResponse();
        PXContext context = perimeterx.pxVerify(customWrapper, new HttpServletResponseWrapper(response));

        Assert.assertNotNull(context, "pxVerify should not return null");
        Assert.assertNotEquals(response.getStatus(), 403, "Request should not be blocked");

        String deValue = (String) customWrapper.getAttribute(DE_HEADER_NAME);
        Assert.assertNotNull(deValue,
                "Data enrichment should be available as a request attribute when request is not a RequestWrapper");
        Assert.assertTrue(deValue.contains("cookieMonster"),
                "Data enrichment attribute should contain the expected PXDE data");
    }

    /**
     * With RequestWrapper: existing behavior is preserved.
     * The DE data should be available via request.getHeader().
     */
    @Test
    public void testRequestWrapper_dataEnrichmentSetAsHeader() throws Exception {
        PXClient client = TestObjectUtils.nonBlockingPXClient(configuration.getBlockingScore());
        PerimeterX perimeterx = TestObjectUtils.testablePerimeterXObject(configuration, client);

        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        RequestWrapper wrappedRequest = new RequestWrapper(mockRequest);

        MockHttpServletResponse response = new MockHttpServletResponse();
        PXContext context = perimeterx.pxVerify(wrappedRequest, new HttpServletResponseWrapper(response));

        Assert.assertNotNull(context, "pxVerify should not return null");
        Assert.assertNotEquals(response.getStatus(), 403, "Request should not be blocked");

        String deHeader = wrappedRequest.getHeader(DE_HEADER_NAME);
        Assert.assertNotNull(deHeader,
                "Data enrichment should be available as a request header when using RequestWrapper");
        Assert.assertTrue(deHeader.contains("cookieMonster"),
                "Data enrichment header should contain the expected PXDE data");
    }

    /**
     * When DE header is not configured, neither header nor attribute should be set,
     * regardless of request type.
     */
    @Test
    public void testNoHeaderConfigured_nothingSet() throws Exception {
        PXConfiguration noDeConfig = PXConfiguration.builder()
                .appId("appId")
                .authToken("token")
                .cookieKey("cookieKey")
                .moduleMode(ModuleMode.BLOCKING)
                .remoteConfigurationEnabled(false)
                .blockingScore(70)
                .build();

        PXClient client = TestObjectUtils.nonBlockingPXClient(noDeConfig.getBlockingScore());
        PerimeterX perimeterx = TestObjectUtils.testablePerimeterXObject(noDeConfig, client);

        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        HttpServletRequest customWrapper = new HttpServletRequestWrapper(mockRequest);

        MockHttpServletResponse response = new MockHttpServletResponse();
        perimeterx.pxVerify(customWrapper, new HttpServletResponseWrapper(response));

        Assert.assertNull(customWrapper.getAttribute(DE_HEADER_NAME),
                "No attribute should be set when DE header name is not configured");
    }
}
