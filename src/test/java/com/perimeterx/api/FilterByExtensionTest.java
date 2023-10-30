package com.perimeterx.api;

import com.perimeterx.http.PXHttpMethod;
import com.perimeterx.models.configuration.PXConfiguration;
import org.springframework.mock.web.MockHttpServletRequest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

public class FilterByExtensionTest {
    private final static String HTML_EXTENSION = "html";
    private final static String CSS_EXTENSION = "css";
    private final static String VALID_PATH_WITH_EXTENSION = "test-path." + CSS_EXTENSION;
    private final static  Set<String> DEFAULT_EXTENSION = new HashSet<>(Arrays.asList(HTML_EXTENSION, CSS_EXTENSION));
    private RequestFilter requestFilter;

    @BeforeMethod
    public void init() {
        final PXConfiguration config = PXConfiguration.builder()
                .staticFilesExt(DEFAULT_EXTENSION)
                .build();
        requestFilter = new RequestFilter(config);
    }

    @Test
    public void testRequestIsFiltered() {
        MockHttpServletRequest req = new MockHttpServletRequest(PXHttpMethod.GET.name(), VALID_PATH_WITH_EXTENSION);

        assertTrue(requestFilter.isExtensionWhiteListed(req.getRequestURI(), req.getMethod()));
    }

    @Test
    public void testRequestIsNotFilteredByMethod() {
        MockHttpServletRequest req = new MockHttpServletRequest(PXHttpMethod.POST.name(), VALID_PATH_WITH_EXTENSION);

        assertFalse(requestFilter.isExtensionWhiteListed(req.getRequestURI(), req.getMethod()));
    }

    @Test
    public void testRequestIsNotFilteredByPath() {
        MockHttpServletRequest req = new MockHttpServletRequest(PXHttpMethod.GET.name(), "test");

        assertFalse(requestFilter.isExtensionWhiteListed(req.getRequestURI(), req.getMethod()));
    }

    @Test
    public void testRequestIsFilteredByDefaultValue() {
        final PXConfiguration config = PXConfiguration.builder().build();
        final RequestFilter reqFilter = new RequestFilter(config);
        final MockHttpServletRequest req = new MockHttpServletRequest(PXHttpMethod.GET.name(), VALID_PATH_WITH_EXTENSION);

        assertTrue(reqFilter.isExtensionWhiteListed(req.getRequestURI(), req.getMethod()));
    }
}
