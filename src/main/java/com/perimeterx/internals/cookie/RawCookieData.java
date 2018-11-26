package com.perimeterx.internals.cookie;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RawCookieData implements Comparable {

    CookieVersion cookieVersion;

    String selectedCookie;

    @Override
    public int compareTo(Object other) {
        return (((RawCookieData) other).getCookieVersion().getVersionLevel() - this.getCookieVersion().getVersionLevel());
    }
}
