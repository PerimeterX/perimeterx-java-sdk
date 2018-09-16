package com.perimeterx.internals.cookie.cookieparsers;

import com.perimeterx.internals.cookie.RawCookieData;
import com.perimeterx.utils.Constants;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class HeaderParser {


    protected abstract String [] splitHeader(String header);

    protected abstract RawCookieData createCookie(String cookie);

    /**
     * This function receives a cookie from the cookie http header, parses it and returns all the available px cookies in a linked list.
     * @param cookieHeader Should contain the cookie(or cookies) that needs to be parsed into RawCookieData, can be null or empty
     * @return All px cookies available from the header.
     * */
    public List<RawCookieData> createRawCookieDataList(String cookieHeader){
        List <RawCookieData> cookieList= new ArrayList<>();
        if (!StringUtils.isEmpty(cookieHeader)) {
            String[] cookies = splitHeader(cookieHeader);
            for (String cookie : cookies) {
                RawCookieData rawCookie = createCookie(cookie);
                if (rawCookie != null){
                    cookieList.add(rawCookie);
                }
            }
        }
        cookieList = reorderCookies(cookieList);
        return cookieList;
    }

    protected List<RawCookieData> reorderCookies(List<RawCookieData> cookieList){
        List <RawCookieData> tempList = new ArrayList<>();
        Iterator<RawCookieData> iter = cookieList.iterator();
        RawCookieData currentCookie;
        while (iter.hasNext()){
            currentCookie = iter.next();
            if (Constants.COOKIE_V3_KEY.equals(currentCookie.getVersion())){
                tempList.add(currentCookie);
                iter.remove();
                break;
            }
        }
        iter = cookieList.iterator();
        while (iter.hasNext()){
            currentCookie = iter.next();
            if (Constants.COOKIE_V1_KEY.equals(currentCookie.getVersion())){
                tempList.add(currentCookie);
                iter.remove();
                break;
            }
        }
        tempList.addAll(cookieList);
        return tempList;
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

    protected static boolean  isValidPxCookie(String version) {
        return "3".equals(version) || "1".equals(version) || Constants.COOKIE_V3_KEY.equals(version) || Constants.COOKIE_V1_KEY.equals(version);

    }
}
