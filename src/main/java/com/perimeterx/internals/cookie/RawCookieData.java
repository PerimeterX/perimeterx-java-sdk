package com.perimeterx.internals.cookie;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.servlet.http.Cookie;

@Data
@AllArgsConstructor
public class RawCookieData {

    CookieVersion cookieVersion;

    String selectedCookie;

}
