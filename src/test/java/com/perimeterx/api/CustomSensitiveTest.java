package com.perimeterx.api;

import com.perimeterx.http.PXClient;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.configuration.PXConfiguration;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.testng.annotations.Test;
import testutils.TestObjectUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import java.io.IOException;
import java.util.function.Predicate;

import static org.junit.Assert.*;

@Test
public class CustomSensitiveTest {
    @Test
    public void testSensitiveByCustomFunction() throws Exception {
        PXConfiguration pxConfiguration = PXConfiguration.builder()
                .appId("PX1234")
                .isSensitiveRequest((req) -> true).build();
        PXClient client = TestObjectUtils.blockingPXClient(pxConfiguration.getBlockingScore());
        PerimeterX perimeterx = TestObjectUtils.testablePerimeterXObject(pxConfiguration, client);
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        PXContext pxContext = perimeterx.pxVerify(request, new HttpServletResponseWrapper(response));

        assertTrue(pxContext.getIsSensitiveRequest().get());
    }

    @Test
    public void testReadingTheBodyManyTimesAndStillOK() throws Exception {
        PXConfiguration pxConfiguration = PXConfiguration.builder()
                .appId("PX1234")
                .isSensitiveRequest((req) -> {
                    try {
                        return req.getReader().readLine().equals("hello") &&
                                req.getReader().readLine().equals("hello") &&
                                req.getReader().readLine().equals("hello") &&
                                req.getReader().readLine().equals("hello");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).build();
        PXClient client = TestObjectUtils.blockingPXClient(pxConfiguration.getBlockingScore());
        PerimeterX perimeterx = TestObjectUtils.testablePerimeterXObject(pxConfiguration, client);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContent("hello".getBytes());
        HttpServletResponse response = new MockHttpServletResponse();
        PXContext pxContext = perimeterx.pxVerify(request, new HttpServletResponseWrapper(response));

        assertTrue(pxContext.getIsSensitiveRequest().get());
    }

    @Test
    public void testCustomFunctionThrowsExceptionShouldNotBeConsideredSensitive() throws Exception {
        PXConfiguration pxConfiguration = PXConfiguration.builder()
                .appId("PX1234")
                .isSensitiveRequest((req) -> {
                    throw new RuntimeException();
                }).build();
        PXClient client = TestObjectUtils.blockingPXClient(pxConfiguration.getBlockingScore());
        PerimeterX perimeterx = TestObjectUtils.testablePerimeterXObject(pxConfiguration, client);
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        PXContext pxContext = perimeterx.pxVerify(request, new HttpServletResponseWrapper(response));

        assertFalse(pxContext.getIsSensitiveRequest().get());
    }

    @Test
    public void testDefaultCustomFunctionShouldReturnFalseAlways() {
        Predicate<? super HttpServletRequest> isSensitiveRequest = PXConfiguration.builder().build().getIsSensitiveRequest();
        assertFalse(isSensitiveRequest.test(null));
        assertFalse(isSensitiveRequest.test(new MockHttpServletRequest()));
    }
}
