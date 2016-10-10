package com.perimeterx.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.perimeterx.internals.cookie.RiskCookie;
import com.perimeterx.models.httpmodels.CaptchaResponse;
import com.perimeterx.models.httpmodels.RiskResponse;

/**
 * JsonUtils - Utility class for Object < - > Json string mapping
 * <p>
 * Created by shikloshi on 10/07/2016.
 */
public final class JsonUtils {

    private final static ObjectMapper mapper = new ObjectMapper();

    public final static ObjectReader riskResponseReader = mapper.reader(RiskResponse.class);
    public final static ObjectReader captchaResponseReader = mapper.reader(CaptchaResponse.class);
    public final static ObjectReader riskCookieReader = mapper.reader(RiskCookie.class);
    public final static ObjectWriter writer = mapper.writer();

    protected JsonUtils() {
    }

}
