package com.perimeterx.api.providers;

import com.perimeterx.models.PXContext;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.risk.CustomParameters;

/**
 * Created by nitzangoldfeder on 03/04/2018.
 */
public interface CustomParametersProvider {
    CustomParameters buildCustomParameters(PXConfiguration pxConfiguration, PXContext pxContext);
}
