package com.perimeterx.utils;

/**
 * Constants container class
 * <p>
 * Created by shikloshi on 10/07/2016.
 */
public final class Constants {

    // This token is replaced by maven on validation stage to the pom version
    public final static String SDK_VERSION = "@moduleVersion@";

    public final static String ACTIVITY_BLOCKED = "block";
    public final static String ACTIVITY_PAGE_REQUESTED = "page_requested";

    public static final String SERVER_URL = "https://sapi.perimeterx.net";
    public final static String COOKIE_CAPTCHA_KEY = "_pxCaptcha";
    public final static String COOKIE_KEY = "_px";

    public static final String API_RISK = "/api/v1/risk";
    public static final String API_ACTIVITIES = "/api/v1/collector/s2s";
    public static final String API_CAPTCHA = "/api/v1/risk/captcha";

    public static final int CAPTCHA_SUCCESS_CODE = 0;
    public static final int CAPTCHA_FAILED_CODE = -1;
}
