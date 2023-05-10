package com.perimeterx.utils;

import com.perimeterx.models.configuration.PXConfiguration;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.message.BasicHeader;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static com.perimeterx.utils.Constants.COOKIE_HEADER_NAME;
import static org.apache.commons.lang3.StringUtils.isNoneEmpty;

/**
 * Created by nitzangoldfeder on 16/07/2017.
 */
public class PXCommonUtils {

    public static List<Header> getDefaultHeaders(String authToken) {
        Header contentType = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        Header authorization = new BasicHeader(HttpHeaders.AUTHORIZATION, "Bearer " + authToken);
        return Arrays.asList(contentType, authorization);
    }

    public static RequestConfig getRequestConfig(PXConfiguration pxConfiguration) {
        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom()
                .setConnectTimeout(pxConfiguration.getConnectionTimeout())
                .setConnectionRequestTimeout(pxConfiguration.getApiTimeout())
                .setSocketTimeout(pxConfiguration.getApiTimeout());
        if (pxConfiguration.isUseProxy()) {
            HttpHost proxy = new HttpHost(pxConfiguration.getProxyHost(), pxConfiguration.getProxyPort());
            requestConfigBuilder.setProxy(proxy);
        }
        return requestConfigBuilder.build();

    }

    public static Map<String, String> getHeadersFromRequest(HttpServletRequest request) {
        HashMap<String, String> headers = new HashMap<>();
        String name;
        Enumeration headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            name = (String) headerNames.nextElement();
            headers.put(name.toLowerCase(), request.getHeader(name));
        }
        return headers;
    }

    public static List<String> cookieHeaders(PXConfiguration configuration) {
        List<String> lst = new LinkedList<>();
        lst.add(COOKIE_HEADER_NAME);
        if(isNoneEmpty(configuration.getCustomCookieHeader())) {
            lst.add(configuration.getCustomCookieHeader());
        }
        return lst;
    }
}
