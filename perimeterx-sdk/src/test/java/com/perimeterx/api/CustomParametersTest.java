package com.perimeterx.api;


import com.perimeterx.api.providers.CustomParametersProvider;
import com.perimeterx.http.ResponseWrapper;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.exceptions.PXException;
import com.perimeterx.models.risk.CustomParameters;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import java.util.function.Function;

import static org.testng.Assert.assertEquals;

@Test
public class CustomParametersTest {
    @Test
    public void testOverrideTheDeprecatedMethod() throws PXException {
        CustomParametersProvider deprecated = (pxConfiguration, pxContext) -> {
            CustomParameters customParameters = new CustomParameters();
            customParameters.setCustomParam1("deprecated");
            return customParameters;
        };


        Function<HttpServletRequest, CustomParameters> current = (req) -> {
            CustomParameters customParameters = new CustomParameters();
            customParameters.setCustomParam1("non deprecated");
            return customParameters;
        };

        PXConfiguration config = PXConfiguration.builder().appId("PX1234")
                .customParametersExtraction(current)
                .customParametersProvider(deprecated).build();

        PerimeterX perimeterX = new PerimeterX(config);
        PXContext pxContext = perimeterX.pxVerify(new MockHttpServletRequest(), new ResponseWrapper(new MockHttpServletResponse()));
        assertEquals(pxContext.getCustomParameters().getCustomParam1(), "non deprecated");
    }

    @Test
    public void testExtractingCustomParametersFromRequest() throws PXException {
        Function<HttpServletRequest, CustomParameters> current = (req) -> {
            CustomParameters customParameters = new CustomParameters();
            customParameters.setCustomParam1(req.getRequestURI());
            return customParameters;
        };

        PXConfiguration config = PXConfiguration.builder().appId("PX1234")
                .customParametersExtraction(current)
                .build();

        PerimeterX perimeterX = new PerimeterX(config);

        MockHttpServletRequest req = new MockHttpServletRequest("get", "/test");

        PXContext pxContext = perimeterX.pxVerify(req, new ResponseWrapper(new MockHttpServletResponse()));
        assertEquals(pxContext.getCustomParameters().getCustomParam1(), "/test");
    }
}

