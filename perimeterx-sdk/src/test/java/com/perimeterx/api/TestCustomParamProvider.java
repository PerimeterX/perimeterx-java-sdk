package com.perimeterx.api;

import com.perimeterx.api.providers.CustomParametersProvider;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.risk.CustomParameters;

/**
 * Created by nitzangoldfeder on 04/04/2018.
 */
public class TestCustomParamProvider implements CustomParametersProvider {

    private CustomParameters customParameters;

    public TestCustomParamProvider(CustomParameters customParameters) {
        this.customParameters = customParameters;
    }

    @Override
    public CustomParameters buildCustomParameters(PXConfiguration pxConfiguration, PXContext pxContext) {
        return customParameters;
    }
}
