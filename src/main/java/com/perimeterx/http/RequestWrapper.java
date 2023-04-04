package com.perimeterx.http;

import com.perimeterx.utils.PXLogger;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Reading HttpServletRequest is limited to one time only
 * This class will read the request and will set its body on the body var
 * This enables reading the request body multiple times
 * **/
public class RequestWrapper extends HttpServletRequestWrapper {
    private final PXLogger logger = PXLogger.getLogger(RequestWrapper.class);
    private String body;
    private final Map<String, String> customHeaders;

    public RequestWrapper(HttpServletRequest request) {
        super(request);
        this.customHeaders = new HashMap<>();
    }

    @Override
    public ServletInputStream getInputStream() {
        String body = "";
        try {
            body = getBody();
        } catch (IOException e) {
            logger.debug("failed to get body", e);
        }
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body.getBytes());
        return new ServletInputStream() {
            public int read() {
                return byteArrayInputStream.read();
            }
        };
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }

    @Override
    public String getHeader(String name) {
        String headerValue = customHeaders.get(name);

        if (headerValue != null){
            return headerValue;
        }
        return ((HttpServletRequest) getRequest()).getHeader(name);
    }

    public void addHeader(String name, String value){
        this.customHeaders.put(name, value);
    }

    public synchronized String getBody() throws IOException {
        if(body == null) {
            this.body = "";

            final BufferedReader bufferedReader = this.getRequest().getReader();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                this.body += line;
            }
        }
        return body;
    }
}
