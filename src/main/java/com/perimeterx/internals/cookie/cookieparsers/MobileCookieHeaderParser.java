package com.perimeterx.internals.cookie.cookieparsers;

import com.perimeterx.internals.cookie.CookieVersion;
import com.perimeterx.internals.cookie.RawCookieData;

public class MobileCookieHeaderParser extends HeaderParser{
    @Override
    protected String[] splitHeader(String header) {
        return header.split(",\\s?");
    }

    @Override
    protected RawCookieData createCookie(String cookie) {
        String [] splitCookie = cookie.split(":\\s?",2);
        RawCookieData rawCookieData = null;
        if (splitCookie.length == 2 && isValidPxCookie(splitCookie[0])){
            String version = splitCookie[0];
            CookieVersion standardVersion = getCookieVersion(version);
            rawCookieData = new RawCookieData(standardVersion, splitCookie[1]);
        }
        else if(splitCookie.length == 1){
            rawCookieData = new RawCookieData(CookieVersion.UNDEFINED, splitCookie[0]);
        }
        return rawCookieData;
    }
}
