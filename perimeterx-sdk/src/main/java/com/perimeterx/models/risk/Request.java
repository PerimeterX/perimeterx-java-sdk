package com.perimeterx.models.risk;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.activities.ActivityHeader;

import java.util.List;

import static com.perimeterx.utils.ActivityUtil.getActivityHeaders;

/**
 * Request model
 * <p>
 * Created by shikloshi on 05/07/2016.
 */
public class Request {
    @JsonProperty("url")
    public String URL;

    @JsonProperty("headers")
    public List<ActivityHeader> headers;

    @JsonProperty("socket_ip")
    private String socketIp;

    public static Request fromContext(PXContext context) {

        Request request = new Request();
        request.socketIp = context.getIp();
        request.URL = context.getFullUrl();
        request.headers = getActivityHeaders(context.getHeaders(), context.getSensitiveHeaders());
        return request;
    }
}