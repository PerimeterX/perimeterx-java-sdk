package com.perimeterx.api;

import com.perimeterx.http.RequestWrapper;
import org.springframework.mock.web.MockHttpServletRequest;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.IOException;

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
}
