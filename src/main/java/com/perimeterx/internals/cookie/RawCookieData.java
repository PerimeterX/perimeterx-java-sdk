package com.perimeterx.internals.cookie;

import lombok.Data;

import java.util.Map;

@Data
public class RawCookieData {

    Map<String, String> cookieMap;

    String selectedCookie;

}
