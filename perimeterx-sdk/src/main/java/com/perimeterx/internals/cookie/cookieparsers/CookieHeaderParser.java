package com.perimeterx.internals.cookie.cookieparsers;

import com.perimeterx.internals.cookie.CookieVersion;
import com.perimeterx.internals.cookie.RawCookieData;
import com.perimeterx.utils.logger.IPXLogger;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class CookieHeaderParser extends HeaderParser {

    public CookieHeaderParser(IPXLogger logger) {
        super(logger);
    }

    @Override
    protected String[] splitHeader(String header) {
        return header.split(";\\s?");
    }

    @Override
    protected RawCookieData createCookie(String cookie) {
        String[] splitCookie = cookie.split("=\\s?", 2);
        RawCookieData rawCookieData = null;
        if (splitCookie.length == 2 && isValidPxCookie(splitCookie[0])) {
            String cookiePayload;
            String version = splitCookie[0];
            try {
                cookiePayload = URLDecoder.decode(splitCookie[1], "UTF-8").replaceAll(" ", "+");
            } catch (UnsupportedEncodingException | IllegalArgumentException e) {
                logger.error("createCookie - failed while decoding cookie", e);
                cookiePayload = splitCookie[1];
            }
            CookieVersion standardVersion = getCookieVersion(version);
            rawCookieData = new RawCookieData(standardVersion, cookiePayload);
        }
        return rawCookieData;
    }
}
