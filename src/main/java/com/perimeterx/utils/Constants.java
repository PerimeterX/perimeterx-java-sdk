package com.perimeterx.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Constants container class
 * <p>
 * Created by shikloshi on 10/07/2016.
 */
public final class Constants {

    private static Properties prop;

    static {
        prop = new Properties();
        InputStream propStream = Constants.class.getResourceAsStream("metadata.properties");
        if (propStream != null) {
            try {
                prop.load(propStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("There is an error, could not found the metadata.properties file");
        }
    }

    public final static String SDK_VERSION = String.valueOf(Constants.prop.get("sdkVersion"));

    public final static String ACTIVITY_BLOCKED = "block";
    public final static String ACTIVITY_PAGE_REQUESTED = "page_requested";

    public static final String SERVER_URL = "https://sapi-%s.glb1.perimeterx.net";
    public final static String COOKIE_CAPTCHA_KEY = "_pxCaptcha";
    public final static String COOKIE_KEY = "_px";

    public static final String API_RISK = "/api/v1/risk";
    public static final String API_ACTIVITIES = "/api/v1/collector/s2s";
    public static final String API_CAPTCHA = "/api/v1/risk/captcha";

    public static final int CAPTCHA_SUCCESS_CODE = 0;
    public static final int CAPTCHA_FAILED_CODE = -1;
}
