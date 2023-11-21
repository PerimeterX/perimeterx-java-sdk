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

    public final static String SDK_VERSION = "Java SDK v" + Constants.prop.get("sdkVersion");

    public final static String ACTIVITY_BLOCKED = "block";
    public final static String ACTIVITY_PAGE_REQUESTED = "page_requested";
    public final static String ACTIVITY_ADDITIONAL_S2S = "additional_s2s";

    public static final String REMOTE_CONFIGURATION_SERVER_URL = "https://px-conf.perimeterx.net";

    public static final String SERVER_URL = "https://sapi-%s.perimeterx.net";
    public static final String COLLECTOR_URL = "https://collector-%s.perimeterx.net";
    public static final String CLIENT_HOST = "client.perimeterx.net";
    public static final String CAPTCHA_HOST = "captcha.px-cdn.net";
    public static final String ALT_CAPTCHA_HOST = "captcha.px-cloud.net";
    public static final String COOKIE_V1_KEY = "_px";
    public static final String COOKIE_V3_KEY = "_px3";
    public static final String DATA_ENRICHMENT = "_pxde";
    public static final String FIRST_PARTY_CAPTCHA_PATH = "/captcha";
    public static final String FIRST_PARTY_VENDOR_PATH = "/init.js";
    public static final String FIRST_PARTY_XHR_PATH = "/xhr";

    public static final String API_RISK = "/api/v3/risk";
    public static final String API_ACTIVITIES = "/api/v1/collector/s2s";
    public static final String API_ENFORCER_TELEMETRY = "/api/v2/risk/telemetry";
    public static final String API_REMOTE_CONFIGURATION = "/api/v1/enforcer";

    public static final int CAPTCHA_SUCCESS_CODE = 0;

    public static final String CAPTCHA_ACTION_CAPTCHA = "c";
    public static final String BLOCK_ACTION_CAPTCHA = "b";
    public static final String BLOCK_ACTION_CHALLENGE = "j";
    public static final String BLOCK_ACTION_RATE = "r";

    public static final String MOBILE_SDK_AUTHORIZATION_HEADER = "x-px-authorization";
    public static final String MOBILE_SDK_ORIGINAL_TOKEN_HEADER = "x-px-original-token";
    public static final String MOBILE_SDK_TOKENS_HEADER = "x-px-tokens";
    public static final String MOBILE_SDK_ORIGINAL_TOKENS_HEADER = "x-px-original-tokens";

    public static String ADDITIONAL_ACTIVITY_HEADER = "px-additional-activity";
    public static String ADDITIONAL_ACTIVITY_URL_HEADER = "px-additional-activity-url";

    public static final String HEADER_ORIGIN = "header";
    public static final String COOKIE_HEADER_NAME = "cookie";

    public static final String FILE_EXTENSION_MUSTACHE = ".mustache";

    public static final String API_COLLECTOR_PREFIX = "https://collector-";
    public static final String API_COLLECTOR_POSTFIX = ".perimeterx.net";

    public static final String CONTENT_TYPE_TEXT_HTML = "text/html";
    public static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";

    public static final String CAPTCHA_BLOCK_TEMPLATE = "captcha_template";
    public static final String BLOCK_TEMPLATE_NAME = "block_template";
    public static final String RATELIMIT_TEMPLATE = "ratelimit";

    public static final String BREACHED_ACCOUNT_KEY_NAME = "breached_account";

    public static final String UNICODE_TYPE = "UTF-8";
    public static final String QUERY_PARAM_PAIRS_SEPARATOR = "&";
    public static final String QUERY_PARAM_KEY_VALUE_SEPARATOR = "=";

    public static final String DEFAULT_LOGIN_RESPONSE_HEADER_NAME = "x-px-login-successful";
    public static final String DEFAULT_LOGIN_RESPONSE_HEADER_VALUE = "1";
    public static final String DEFAULT_COMPROMISED_CREDENTIALS_HEADER_NAME = "px-compromised-credentials";

    public static final String DEFAULT_TELEMETRY_REQUEST_HEADER_NAME = "x-px-enforcer-telemetry";

    public final static String URL_HTTPS_PREFIX = "https://";
    public final static String CAPTCHA_FIRST_PARTY_FILE_PATH = "/captcha.js";
    public final static String SENSOR_FIRST_PARTY_PATH = "/main.min.js";
    public final static char SLASH = '/';
    public final static char QUESTION_MARK = '?';
    public final static String XHR_PATH = "xhr";
    public final static String FIRST_PARTY_HEADER_NAME = "X-PX-FIRST-PARTY";
    public final static String FIRST_PARTY_HEADER_VALUE = "1";
}
