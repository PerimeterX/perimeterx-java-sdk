package com.perimeterx.internals.cookie;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CookieData {

    private String pxCookie;
    private String userAgent;
    private String ip;
    private boolean mobileToken;
    private String cookieOrig;
    private String cookieVersion;


}
