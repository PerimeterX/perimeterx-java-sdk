package com.perimeterx.utils;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class CookieNamesExtractorTest {

    String cookieHeader = "_px3=px3Cookie;tempCookie=CookieTemp; _px7=NotARealCookie";

    @Test
    public void testExtractCookieNames() {
        String[] cookies = CookieNamesExtractor.extractCookieNames(cookieHeader);
        assertEquals(cookies[0], "_px3");
        assertEquals(cookies[1], "tempCookie");
        assertEquals(cookies[2], "_px7");
    }
}
