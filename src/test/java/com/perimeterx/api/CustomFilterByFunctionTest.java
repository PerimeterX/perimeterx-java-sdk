package com.perimeterx.api;

import com.perimeterx.models.configuration.PXConfiguration;
import org.springframework.mock.web.MockHttpServletRequest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;

import java.util.function.Predicate;

import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

public class CustomFilterByFunctionTest {
    private final static Predicate<HttpServletRequest> TRUTHY_REQUEST_FILTER = req -> true;
    private final static Predicate<HttpServletRequest> FALSY_REQUEST_FILTER = req -> false;
    HttpServletRequest req;

    @BeforeMethod
    private void init() {
        req = new MockHttpServletRequest();
    }

    @Test
    public void testRequestIsFiltered()  {
        final RequestFilter requestFilter = getRequestFilter(TRUTHY_REQUEST_FILTER);

        assertTrue(requestFilter.isFilteredByCustomFunction(req));
    }

    @Test
    public void testRequestIsNotFiltered() {
        final RequestFilter requestFilter = getRequestFilter(FALSY_REQUEST_FILTER);

        assertFalse(requestFilter.isFilteredByCustomFunction(req));
    }

    @Test
    public void testExceptionHandlingShouldReturnFalse() {
        final RequestFilter requestFilter = getRequestFilter((req) -> {throw new RuntimeException();});

        assertFalse(requestFilter.isFilteredByCustomFunction(req));
    }

    @Test
    public void testDefaultBehaviorShouldReturnFalse() {
        final RequestFilter requestFilter = getRequestFilter(null);

        assertFalse(requestFilter.isFilteredByCustomFunction(req));
    }

    private RequestFilter getRequestFilter(Predicate<HttpServletRequest> filterByCustomFunction) {
        final PXConfiguration config = PXConfiguration.builder()
                .filterByCustomFunction(filterByCustomFunction)
                .build();

        return new RequestFilter(config);
    }
}
