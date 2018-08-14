package com.perimeterx.internals.cookie.cookieparsers;

import com.perimeterx.internals.cookie.RawCookieData;
import com.perimeterx.utils.Constants;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class HeaderParser {


    protected abstract String [] splitHeader(String header);

    protected abstract void addCookie(String cookie, Map<String, String> cookieMap);


    public RawCookieData createRawCookieData(String cookieHeader){
        RawCookieData rawCookieData = new RawCookieData();
        rawCookieData.setSelectedCookie(cookieHeader);
        Map<String, String> cookieMap = new LinkedHashMap<>();
        if (!StringUtils.isEmpty(cookieHeader)) {
            String[] cookies = splitHeader(cookieHeader);
            for (String cookie : cookies) {
                addCookie(cookie, cookieMap);
            }
        }
        rawCookieData.setCookieMap(cookieMap);
        rawCookieData.setSelectedCookie(cookieHeader);
        return rawCookieData;
    }



    protected static void putInCookieByVersionName(Map<String, String> cookieMap, String version, String cookiePayload) {
        switch (version) {
            case "3":
                cookieMap.put(Constants.COOKIE_V3_KEY, cookiePayload);
                break;
            case "1":
                cookieMap.put(Constants.COOKIE_V1_KEY, cookiePayload);
                break;
            case Constants.COOKIE_V1_KEY:
                cookieMap.put(Constants.COOKIE_V1_KEY, cookiePayload);
                break;
            case Constants.COOKIE_V3_KEY:
                cookieMap.put(Constants.COOKIE_V3_KEY, cookiePayload);
                break;
        }
    }
}
