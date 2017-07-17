package com.perimeterx.utils;

import com.google.common.collect.Lists;
import com.google.common.net.MediaType;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.message.BasicHeader;

import java.util.Arrays;
import java.util.List;

/**
 * Created by nitzangoldfeder on 16/07/2017.
 */
public class PXCommonUtils {
    
    public static List<Header> getDefaultHeaders(String authToken){
        Header contentType = new BasicHeader(HttpHeaders.CONTENT_TYPE, MediaType.ANY_APPLICATION_TYPE.toString());
        Header authorization = new BasicHeader(HttpHeaders.AUTHORIZATION, authToken);
        return Arrays.asList(contentType, authorization);
    }

    public static RequestConfig getRequestConfig(int connectionTimeout, int apiTimeout){
        return RequestConfig.custom()
                    .setConnectTimeout(connectionTimeout)
                    .setConnectionRequestTimeout(apiTimeout)
                    .build();

    }
}
