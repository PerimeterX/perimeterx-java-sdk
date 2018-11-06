package com.perimeterx.utils;

import org.testng.annotations.Test;
import javax.servlet.http.Cookie;
import static org.testng.Assert.assertEquals;

public class CookieNamesExtractorTest {

    String cookieHeader = "_px3=px3Cookie;tempCookie=CookieTemp; _px7=NotARealCookie";

    @Test
    public void testExtractCookieNames(){
        Cookie px3 = new Cookie("_px3","px3Cookie");
        Cookie tempCookie = new Cookie("tempCookie","CookieTemp");
        Cookie px7 = new Cookie("_px7","NotARealCookie");
        Cookie [] cookies = {px3, tempCookie, px7};

        String[] cookiesAsStr = CookieNamesExtractor.extractCookieNames(cookies);
        assertEquals(cookiesAsStr[0],"_px3");
        assertEquals(cookiesAsStr[1],"tempCookie");
        assertEquals(cookiesAsStr[2],"_px7");
    }
}
