package com.perimeterx.utils;

import com.perimeterx.models.configuration.PXConfiguration;

import java.net.URI;

import static com.perimeterx.utils.Constants.*;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class FirstPartyUtil {
    public static boolean isValidFirstPartyRequest(PXConfiguration config, URI uri) {
        final String url = URL_HTTPS_PREFIX + uri.getHost() + uri.getPath();
        final String captchaURL = getFirstPartyCaptchaURL(config);
        final String sensorURL = getFirstPartySensorURL(config);
        final String xhrURL = getFirstPartyXhrUrl(config, uri.getPath(), false);

        return url.equals(captchaURL) ||
                url.equals(sensorURL) ||
                url.equals(xhrURL);
    }

    public static String getFirstPartyCaptchaURL(PXConfiguration config) {
        return getFirstPartyCaptchaURL(config, null, false);
    }

    public static String getFirstPartyCaptchaURL(PXConfiguration config, String params, boolean alternativeCaptcha) {
        final String host = alternativeCaptcha ? ALT_CAPTCHA_HOST : CAPTCHA_HOST;
        String url = URL_HTTPS_PREFIX + host + SLASH + config.getAppId() + CAPTCHA_FIRST_PARTY_FILE_PATH;

        if (!isBlank(params)) {
            url += QUESTION_MARK + params;
        }
        return url;
    }

    public static String getFirstPartySensorURL(PXConfiguration config) {
        final String path = String.format("/%s%s", config.getAppId(), SENSOR_FIRST_PARTY_PATH);
        return  URL_HTTPS_PREFIX + config.getClientHost() + path;
    }

    public static String getFirstPartyXhrUrl(PXConfiguration config, String requestURI, boolean substringRawPath) {
        if (isBlank(requestURI)) {
            return null;
        }
        final String firstPartyAppIdForm = config.getAppId().substring(2);
        String path = requestURI;

        if (substringRawPath) {
            final String xhrPrefix = String.format("/%s/%s", firstPartyAppIdForm, XHR_PATH);
            path = requestURI.substring(xhrPrefix.length());
        }
        return config.getCollectorUrl() + path;
    }
}
