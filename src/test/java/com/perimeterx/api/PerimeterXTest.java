package com.perimeterx.api;

import com.perimeterx.http.PXClient;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.testng.Assert;
import org.testng.annotations.Test;
import testutils.ConfiguredTest;
import testutils.TestObjectUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * Created by shikloshi on 13/07/2016.
 */

@Test
public class PerimeterXTest extends ConfiguredTest {

    private PXConfiguration configuration;

    @Override
    public void testSetup() throws Exception {
        configuration = TestObjectUtils.generateConfiguration();
    }

    @Test
    public void testPxVerify_notVerified() throws Exception {
        PXClient client = TestObjectUtils.blockingPXClient(configuration.getBlockingScore());
        PerimeterX perimeterx = TestObjectUtils.testablePerimeterXObject(configuration, client);
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        perimeterx.pxVerify(request, new HttpServletResponseWrapper(response));
        Assert.assertEquals(response.getStatus(), 403);
    }

    @Test
    public void testPxVerify_verified() throws Exception {
        System.out.println(configuration);
        PXClient client = TestObjectUtils.nonBlockingPXClient(configuration.getBlockingScore());
        PerimeterX perimeterx = TestObjectUtils.testablePerimeterXObject(configuration, client);
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        perimeterx.pxVerify(request, new HttpServletResponseWrapper(response));
        Assert.assertNotEquals(response.getStatus(), 403);
    }
}
