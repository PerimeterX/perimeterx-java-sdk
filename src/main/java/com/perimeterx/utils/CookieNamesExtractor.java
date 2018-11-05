package com.perimeterx.utils;

public class CookieNamesExtractor {

    public static String[] extractCookieNames (String cookieHeader){
        String[] cookieNames = null;
        if (cookieHeader != null) {
            String[] cookies = cookieHeader.split(";\\s?");
            cookieNames = new String[cookies.length];
            for (int i = 0; i < cookies.length; i++) {
                String[] keyValue = cookies[i].split("=\\s?");
                cookieNames[i] = keyValue[0];
            }
        }
        return cookieNames;
    }
}
