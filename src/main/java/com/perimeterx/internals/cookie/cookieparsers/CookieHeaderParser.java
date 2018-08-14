package com.perimeterx.internals.cookie.cookieparsers;


import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

public class CookieHeaderParser extends HeaderParser {


    @Override
    protected String[] splitHeader(String header) {
        return header.split(";\\s?");
    }


    @Override
    protected void addCookie(String cookie, Map<String, String> cookieMap) {
        String[] splitCookie = cookie.split("=\\s?",2);
        if (splitCookie.length == 2){
            String cookiePayload;
            String version = splitCookie[0];
            try {
                cookiePayload = URLDecoder.decode(splitCookie[1], "UTF-8").replaceAll(" ", "+");
            } catch (UnsupportedEncodingException e) {
                cookiePayload = splitCookie[1];
            }
            putInCookieByVersionName(cookieMap, version, cookiePayload);
        }
    }
}
