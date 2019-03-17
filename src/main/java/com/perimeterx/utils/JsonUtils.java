package com.perimeterx.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.perimeterx.models.configuration.PXDynamicConfiguration;
import com.perimeterx.models.httpmodels.RiskResponse;

import java.io.IOException;

/**
 * JsonUtils - Utility class for Object to Json string mapping
 * <p>
 * Created by shikloshi on 10/07/2016.
 */
public final class JsonUtils {

    private final static ObjectMapper mapper = new ObjectMapper();

    public final static ObjectReader riskResponseReader = mapper.readerFor(RiskResponse.class);
    public final static ObjectReader pxConfigurationStubReader = mapper.readerFor(PXDynamicConfiguration.class);
    public final static ObjectWriter writer = mapper.writer();

    protected JsonUtils() {
    }

    static void readJsonStringIntoObject(Object object, String content) throws IOException {
        mapper.readerForUpdating(object).readValue(content);
    }
}
