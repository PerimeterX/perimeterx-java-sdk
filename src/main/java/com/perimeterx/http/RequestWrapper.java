package com.perimeterx.http;

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
 **/
public class RequestWrapper extends HttpServletRequestWrapper {
    private String body;
    private final Map<String, String> customHeaders;
    private static final int BUFFER_SIZE = 4096;

    public RequestWrapper(HttpServletRequest request) {
        super(request);
        this.customHeaders = new HashMap<>();
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(getBody().getBytes());
        return new ServletInputStreamWrapper(byteArrayInputStream);
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }

    @Override
    public String getHeader(String name) {
        String headerValue = customHeaders.get(name);

        if (headerValue != null) {
            return headerValue;
        }
        return ((HttpServletRequest) getRequest()).getHeader(name);
    }

    public void addHeader(String name, String value) {
        this.customHeaders.put(name, value);
    }

    public synchronized String getBody() throws IOException {
        if (body == null) {
            this.body = "";
            char[] buffer = new char[BUFFER_SIZE];

            try (BufferedReader reader = this.getRequest().getReader()) {
                StringBuilder builder = new StringBuilder();
                int numChars;
                while ((numChars = reader.read(buffer)) >= 0) {
                    builder.append(buffer, 0, numChars);
                }
                body = builder.toString();
            }
        }
        return body;
    }

    private static class ServletInputStreamWrapper extends ServletInputStream {
        private final ByteArrayInputStream inputStream;

        public ServletInputStreamWrapper(ByteArrayInputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public int read() {
            return inputStream.read();
        }

        @Override
        public void close() throws IOException {
            super.close();
            inputStream.close();
        }
    }
}
