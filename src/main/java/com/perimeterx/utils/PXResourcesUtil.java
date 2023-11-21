package com.perimeterx.utils;

import com.perimeterx.models.configuration.PXConfiguration;
import org.apache.http.client.methods.HttpRequestBase;

import static com.perimeterx.utils.Constants.*;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class PXResourcesUtil {
    public static boolean isValidPxThirdPartyRequest(HttpRequestBase req) {
        if (req != null && req.getHeaders(FIRST_PARTY_HEADER_NAME).length > 0) {
            return FIRST_PARTY_HEADER_VALUE
                    .equals(req.getFirstHeader(FIRST_PARTY_HEADER_NAME).getValue());
        }
        return false;
    }

    public static String getPxCaptchaURL(PXConfiguration config, String params, boolean alternativeCaptcha) {
        final String host = alternativeCaptcha ? ALT_CAPTCHA_HOST : CAPTCHA_HOST;
        String url = URL_HTTPS_PREFIX + host + SLASH + config.getAppId() + CAPTCHA_FIRST_PARTY_FILE_PATH;

        if (!isBlank(params)) {
            url += QUESTION_MARK + params;
        }
        return url;
    }

    public static String getPxSensorURL(PXConfiguration config) {
        final String path = String.format("/%s%s", config.getAppId(), SENSOR_FIRST_PARTY_PATH);
        return  URL_HTTPS_PREFIX + config.getClientHost() + path;
    }

    public static String getPxXhrUrl(PXConfiguration config, String requestURI) {
        if (isBlank(requestURI)) {
            return null;
        }
        final String firstPartyAppIdForm = config.getAppId().substring(2);
        final String xhrPrefix = String.format("/%s/%s", firstPartyAppIdForm, XHR_PATH);
        final String path = requestURI.substring(xhrPrefix.length());

        return config.getCollectorUrl() + path;
    }
}
