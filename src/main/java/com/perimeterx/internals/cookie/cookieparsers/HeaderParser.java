package com.perimeterx.internals.cookie.cookieparsers;

import com.perimeterx.internals.cookie.RawCookieData;
import com.perimeterx.utils.Constants;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class HeaderParser {


    protected abstract String [] splitHeader(String header);

    protected abstract RawCookieData createCookie(String cookie);

    /**
     * @param cookieHeader Should contain the cookie(or cookies) that needs to be parsed into RawCookieData, can be null or empty
     * */
    public List<RawCookieData> createRawCookieDataList(String cookieHeader){
        List <RawCookieData> cookieList= new ArrayList<>();
        if (!StringUtils.isEmpty(cookieHeader)) {
            String[] cookies = splitHeader(cookieHeader);
            for (String cookie : cookies) {
                cookieList.add(createCookie(cookie));
            }
        }
        return cookieList;
    }



    protected static String  putInCookieByVersionName(String version) {
        switch (version) {
            case "3":
                return Constants.COOKIE_V3_KEY;
            case "1":
                return Constants.COOKIE_V1_KEY;
            case Constants.COOKIE_V1_KEY:
                return Constants.COOKIE_V1_KEY;
            case Constants.COOKIE_V3_KEY:
                return Constants.COOKIE_V3_KEY;
            default:
                return "";

        }
    }
}
