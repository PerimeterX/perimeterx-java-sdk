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
    public static final String CAPTCHA_HOST = "captcha.px-cdn.net";
    public static final String COOKIE_CAPTCHA_KEY = "_pxCaptcha";
    public static final String COOKIE_V1_KEY = "_px";
    public static final String COOKIE_V1_MOBILE_VALUE = "1";
    public static final String COOKIE_V3_KEY = "_px3";
    public static final String FIRST_PARTY_CAPTCHA_PATH = "/captcha";
    public static final String FIRST_PARTY_VENDOR_PATH = "/init.js";
    public static final String FIRST_PARTY_XHR_PATH = "/xhr";


    public static final String API_RISK = "/api/v2/risk";
    public static final String API_ACTIVITIES = "/api/v1/collector/s2s";
    public static final String API_ENFORCER_TELEMETRY = "/api/v2/risk/telemetry";
    public static final String API_REMOTE_CONFIGURATION = "/api/v1/enforcer/";

    public static final int CAPTCHA_SUCCESS_CODE = 0;

    public static final String CAPTCHA_ACTION_CAPTCHA = "c";
    public static final String BLOCK_ACTION_CAPTCHA = "b";
    public static final String BLOCK_ACTION_CHALLENGE = "j";
    public static final String BLOCK_ACTION_RATE = "r";



    public static final String MOBILE_SDK_AUTHORIZATION_HEADER = "x-px-authorization";
    public static final String MOBILE_SDK_ORIGINAL_TOKEN_HEADER = "x-px-original-token";
    public static final String MOBILE_SDK_TOKENS_HEADER = "x-px-tokens";
    public static final String MOBILE_SDK_ORIGINAL_TOKENS_HEADER = "x-px-original-tokens";

    public static final String HEADER_ORIGIN = "header";
    public static final String COOKIE_HEADER_NAME = "cookie";

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
    public static final String CAPTCHA_BLOCK_TEMPLATE = "captcha_template";
    public static final String BLOCK_TEMPLATE = "block_template";
    public static final String RATELIMIT_TEMPLATE = "ratelimit";




}
