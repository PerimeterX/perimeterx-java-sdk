package com.perimeterx.http;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.*;


/** This wrapper enables reading the response body multiple times **/
public class ResponseWrapper extends HttpServletResponseWrapper {
    private final static int BUFFER_SIZE = 1024;

    private final StringWriter stringWriter = new StringWriter(BUFFER_SIZE);

    public ResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public PrintWriter getWriter() {
        return new PrintWriter(stringWriter);
    }

    @Override
    public ServletOutputStream getOutputStream() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return stringWriter.toString();
    }
}