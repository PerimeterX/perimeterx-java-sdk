package com.perimeterx.internals.cookie.cookieparsers;

import java.util.Map;

public class MobileCookieHeaderParser extends HeaderParser{
    @Override
    protected String[] splitHeader(String header) {
        return header.split(",\\s?");
    }

    @Override
    protected void addCookie(String cookie, Map<String, String> cookieMap) {
        String[] splitCookie = cookie.split(":\\s?",2);
        if (splitCookie.length == 2){
            String version = splitCookie[0];
            putInCookieByVersionName(cookieMap, version, splitCookie[1]);
        }
    }
}
