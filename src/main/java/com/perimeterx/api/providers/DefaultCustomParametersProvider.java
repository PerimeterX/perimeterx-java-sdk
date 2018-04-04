package com.perimeterx.api.providers;

import com.perimeterx.models.PXContext;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.risk.CustomParameters;

/**
 * Created by nitzangoldfeder on 03/04/2018.
 * Default implementation of Custom Params Provider
 */
public class DefaultCustomParametersProvider implements CustomParametersProvider{
    @Override
    public CustomParameters buildCustomParameters(PXConfiguration pxConfiguration, PXContext pxContext) {
        return new CustomParameters();
    }
}
