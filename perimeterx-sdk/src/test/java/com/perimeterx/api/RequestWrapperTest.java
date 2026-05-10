package com.perimeterx.api;

import com.perimeterx.http.RequestWrapper;
import org.springframework.mock.web.MockHttpServletRequest;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.testng.Assert.*;

@Test
public class RequestWrapperTest {
    @Test
    public void testReadLinesFromRequest() throws IOException {
        MockHttpServletRequest req = new MockHttpServletRequest();
        String s = "line1\nline2\nline3\n\n";
        req.setContent(s.getBytes());
        RequestWrapper requestWrapper = new RequestWrapper(req);
        BufferedReader reader = requestWrapper.getReader();
        assertEquals(reader.readLine(), "line1");
        assertEquals(reader.readLine(), "line2");
        assertEquals(reader.readLine(), "line3");
        assertEquals(reader.readLine(), "");
        assertNull(reader.readLine());
    }

    @Test
    public void testReadMultipleTimes() throws IOException {
        MockHttpServletRequest req = new MockHttpServletRequest();
        String s = "line1\nline2\nline3\n\n";
        req.setContent(s.getBytes());
        RequestWrapper requestWrapper = new RequestWrapper(req);
        BufferedReader reader = requestWrapper.getReader();
        BufferedReader reader2 = requestWrapper.getReader();
        assertEquals(reader.readLine(), "line1");
        assertEquals(reader.readLine(), "line2");
        assertEquals(reader.readLine(), "line3");
        assertEquals(reader.readLine(), "");
        assertEquals(reader2.readLine(), "line1");
        assertEquals(reader2.readLine(), "line2");
        assertEquals(reader2.readLine(), "line3");
        assertEquals(reader2.readLine(), "");
        assertNull(reader2.readLine());
    }

    @Test
    public void testReadingTheBody() throws IOException {
        MockHttpServletRequest req = new MockHttpServletRequest();
        String s = "line1\nline2\nline3\n\n";
        req.setContent(s.getBytes());
        RequestWrapper requestWrapper = new RequestWrapper(req);
        assertEquals(requestWrapper.getBody(),s);
    }

    @Test
    public void testSpecialCharacters() throws IOException {
        byte[] bytes = new byte[255];
        for (int i = 0; i < 255; i++) {
            bytes[i] = (byte) i;
        }
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setContent(bytes);
        RequestWrapper requestWrapper = new RequestWrapper(req);
        assertEquals(requestWrapper.getBody(), new String(bytes));
    }

    @Test
    public void testGetHeader() {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addHeader("header1", "value1");
        RequestWrapper requestWrapper = new RequestWrapper(req);
        requestWrapper.addHeader("header2", "value2");

        assertEquals(requestWrapper.getHeader("header1"), "value1");
        assertEquals(requestWrapper.getHeader("header2"), "value2");
    }

    @Test
    public void testGetHeaderNames() {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addHeader("header1", "value1");
        RequestWrapper requestWrapper = new RequestWrapper(req);
        requestWrapper.addHeader("header2", "value2");

        boolean foundHeader1 = false;
        boolean foundHeader2 = false;
        for (String headerName : Collections.list(requestWrapper.getHeaderNames())) {
            if (headerName.equals("header1")) {
                foundHeader1 = true;
            }
            if (headerName.equals("header2")) {
                foundHeader2 = true;
            }
        }
        assertTrue(foundHeader1);
        assertTrue(foundHeader2);
    }

    @Test
    public void testGetHeaders() {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addHeader("header1", "value1");
        RequestWrapper requestWrapper = new RequestWrapper(req);
        requestWrapper.addHeader("header2", "value2");

        List<String> header1Values = Collections.list(requestWrapper.getHeaders("header1"));
        assertEquals(header1Values.size(), 1);
        for (String headerValue : header1Values) {
            assertEquals(headerValue, "value1");
        }

        List<String> header2Values = Collections.list(requestWrapper.getHeaders("header2"));
        assertEquals(header2Values.size(), 1);
        for (String headerValue : header2Values) {
            assertEquals(headerValue, "value2");
        }
    }

    @Test
    public void testGetIntHeader() {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addHeader("intHeader", "123");
        RequestWrapper requestWrapper = new RequestWrapper(req);
        requestWrapper.addHeader("customIntHeader", "456");
        requestWrapper.addHeader("stringHeader", "stringValue");

        assertEquals(requestWrapper.getIntHeader("intHeader"), 123);
        assertEquals(requestWrapper.getIntHeader("customIntHeader"), 456);
        assertEquals(requestWrapper.getIntHeader("nonExistentHeader"), -1);
        try {
            requestWrapper.getIntHeader("stringHeader");
            fail("Expected NumberFormatException");
        } catch (NumberFormatException e) {
            // Expected exception
        }
    }

    @Test
    public void testGetDateHeader() {
        MockHttpServletRequest req = new MockHttpServletRequest();
        long now = System.currentTimeMillis();
        req.addHeader("dateHeader", Long.toString(now));
        RequestWrapper requestWrapper = new RequestWrapper(req);
        requestWrapper.addHeader("customDateHeader", Long.toString(now + 1000));
        requestWrapper.addHeader("stringHeader", "stringValue");

        assertEquals(requestWrapper.getDateHeader("dateHeader"), now);
        assertEquals(requestWrapper.getDateHeader("customDateHeader"), now + 1000);
        assertEquals(requestWrapper.getDateHeader("nonExistentHeader"), -1);
        try {
            requestWrapper.getDateHeader("stringHeader");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected exception
        }
    }
}
