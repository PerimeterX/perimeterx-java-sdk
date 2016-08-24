package com.perimeterx.models.risk;

import com.perimeterx.models.PXContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Request model
 * <p>
 * Created by shikloshi on 05/07/2016.
 */
public class Request {

    private String ip;
    private String uri;
    private String url;
    private List<Map.Entry<String, String>> headers;

    public Request() {
    }

    public Request(PXContext context) {
        this.headers = new ArrayList<>();
        for (Map.Entry<String, String> h: context.getHeaders().entrySet()) {
            this.headers.add(new HeaderEntry(h.getKey(), h.getValue()));
        };
        this.ip = context.getIp();
        this.uri = context.getUri();
        this.url = context.getFullUrl();
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setHeaders(List<Map.Entry<String, String>> headers) {
        this.headers = headers;
    }

    public String getIp() {
        return ip;
    }

    public String getUri() {
        return uri;
    }

    public String getUrl() {
        return url;
    }

    public List<Map.Entry<String, String>> getHeaders() {
        return headers;
    }
}
