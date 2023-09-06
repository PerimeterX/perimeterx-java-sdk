package com.perimeterx.http;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;

import java.io.IOException;
import java.io.InputStream;

import static java.nio.charset.StandardCharsets.UTF_8;

public class PXApacheIncomingResponse implements IPXIncomingResponse {
    private final CloseableHttpResponse response;

    public PXApacheIncomingResponse(CloseableHttpResponse response) {
        this.response = response;
    }

    @Override
    public InputStream body() throws IOException {
        HttpEntity entity = response.getEntity();
        if (entity == null) {
            return null;
        }

        return entity.getContent();
    }

    @Override
    public PXHttpStatus status() {
        StatusLine statusLine = response.getStatusLine();
        return new PXHttpStatus(statusLine.getStatusCode(), statusLine.getReasonPhrase());
    }

    @Override
    public PXHttpHeader[] headers() {
        Header[] allHeaders = response.getAllHeaders();
        PXHttpHeader[] headers = new PXHttpHeader[allHeaders.length];
        for (int i = 0; i < allHeaders.length; i++) {
            headers[i] = new PXHttpHeader(allHeaders[i].getName(), allHeaders[i].getValue());
        }
        return headers;
    }

    @Override
    public void close() throws IOException {
        if (response != null) {
            response.close();
        }
    }
}
