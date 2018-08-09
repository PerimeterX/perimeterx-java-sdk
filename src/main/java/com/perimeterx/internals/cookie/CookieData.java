package com.perimeterx.internals.cookie;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Builder
@Data
public class CookieData {

    private Map<String, String> pxCookies;
    private String userAgent;
    private String ip;
    private boolean mobileToken;
    private String cookie;

}
