package com.perimeterx.utils;

import javax.servlet.http.Cookie;
import java.util.ArrayList;
import java.util.List;

public class CookieNamesExtractor {

    public static String[] extractCookieNames (Cookie[] cookies){
        List <String> cookiesList = new ArrayList<>();
        if (cookies != null) {
            cookiesList = new ArrayList<>();
            for (Cookie cookie : cookies) {
                cookiesList.add(cookie.getName());
            }
        }
        return cookiesList.toArray(new String[cookiesList.size()]);
    }
}
