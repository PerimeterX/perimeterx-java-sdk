package com.perimeterx.utils;

/**
 * Constants container class
 * <p>
 * Created by shikloshi on 10/07/2016.
 */
public final class Constants {

    public final static String MODULE = "JAVA SDK v1.0";

    public final static String ACTIVITY_BLOCKED = "blocked";
    public final static String ACTIVITY_PAGE_REQUESTED = "page_requested";

    public static final String SERVER_URL = "https://sapi-cdn.perimeterx.net";
    public final static String COOKIE_CAPTCHA_KEY = "_pxCaptcha";
    public final static String COOKIE_KEY = "_px";

    public static final String API_RISK = "/api/v1/risk";
    public static final String API_ACTIVITIES = "/api/v1/collector/s2s";
    public static final String API_CAPTCHA = "/api/v1/risk/captcha";

    public static final int CAPTCHA_SUCCESS_CODE = 0;
    public static final int CAPTCHA_FAILED_CODE = -1;
}
