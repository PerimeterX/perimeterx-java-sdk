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

    public final static String SDK_VERSION = new StringBuilder().append("Java SDK v").append(String.valueOf(Constants.prop.get("sdkVersion"))).toString();

    public final static String ACTIVITY_BLOCKED = "block";
    public final static String ACTIVITY_PAGE_REQUESTED = "page_requested";

    public static final String REMOTE_CONFIGURATION_SERVER_URL = "https://px-conf.perimeterx.net";

    public static final String SERVER_URL = "https://sapi-%s.perimeterx.net";
    public static final String COLLECTOR_URL = "https://collector-%s.perimeterx.net";
    public static final String CLIENT_HOST = "https://client.perimeterx.net";
    public static final String COOKIE_CAPTCHA_KEY = "_pxCaptcha";
    public static final String COOKIE_V1_KEY = "_px";
    public static final String COOKIE_V1_MOBILE_VALUE = "1";
    public static final String COOKIE_V3_KEY = "_px3";
    public static final String FIRST_PARTY_HEADER = "x-px-first-party";
    public static final String ENFORCER_TRUE_IP_HEADER = "x-px-enforcer-true-ip";
    public static final String FIRST_PARTY_VALUE = "1";

    public static final String API_RISK = "/api/v2/risk";
    public static final String API_ACTIVITIES = "/api/v1/collector/s2s";
    public static final String API_ENFORCER_TELEMETRY = "/api/v2/risk/telemetry";
    public static final String API_CAPTCHA = "/api/v2/risk/captcha";
    public static final String API_REMOTE_CONFIGURATION = "/api/v1/enforcer/";

    public static final int CAPTCHA_SUCCESS_CODE = 0;
    public static final int CAPTCHA_FAILED_CODE = -1;

    public static final String CAPTCHA_ACTION_CAPTCHA = "c";
    public static final String BLOCK_ACTION_CAPTCHA = "b";
    public static final String BLOCK_ACTION_CHALLENGE = "j";

    public static final String MOBILE_SDK_HEADER = "x-px-authorization";
    public static final String COOKIE_EXTRACT_DELIMITER_MOBILE = ":";

    public static final String HEADER_ORIGIN = "header";
    public static final String COOKIE_ORIGIN = "cookie";

    public static final String MOBILE_ERROR_NO_COOKIE = "1";
    public static final String MOBILE_ERROR_NO_CONNECTION = "2";
    public static final String MOBILE_ERROR_PINNING = "3";

    public static final String FILE_NAME_BLOCK = "block";
    public static final String FILE_EXTENSION_MUSTACHE = ".mustache";
    public static final String FILE_NAME_MOBILE = ".mobile";
    public static final String FILE_NAME_CAPTCHA = "captcha";
    public static final String FILE_NAME_FUN_CAPTCHA = "funcaptcha";

    public static final String API_COLLECTOR_PREFIX = "https://collector-";
    public static final String API_COLLECTOR_POSTFIX = ".perimeterx.net";

    public static final String CONTENT_TYPE_TEXT_HTML = "text/html";
    public static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";

    public static final String MOBILE_ACTION_CAPTCHA = "captcha";
    public static final String MOBILE_ACTION_BLOCK = "block";


}
