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
    private static final PXLogger logger = PXLogger.getLogger(PXCommonUtils.class);

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

    public static List<String> cookieHeadersNames(PXConfiguration configuration) {
        List<String> lst = new LinkedList<>();

        if (isNoneEmpty(configuration.getCustomCookieHeader())) {
            lst.add(configuration.getCustomCookieHeader());
        }
        lst.add(COOKIE_HEADER_NAME);

        return lst;
    }

    /**
     * Logs the runtime of a given function. returns the result of the execution
     * whether it's a Throwable or value.
     *
     * @param methodName the name of the method to be logged
     * @param f          the function to execute
     * @param <T>        type of successful execution
     * @param <E>        type of failed execution
     * @return the success result of the execution
     * @throws E the throwable that will be thrown if the function fails
     */
    public static <T, E extends Throwable> T logTime(String methodName, PXCallable<T, E> f) throws E {
        long s = System.currentTimeMillis();
        try {
            return f.apply();
        } finally {
            long e = System.currentTimeMillis();
            logger.debug(String.format("TIMELOGGER - %s execution time is (%d)ms", methodName, e - s));
        }
    }

    public static <E extends Throwable> void logTime(String methodName, PXCallableVoid<E> f) throws E {
        // Using the other signature.
        logTime(methodName, () -> {
            f.apply();
            return null;
        });
    }
}
