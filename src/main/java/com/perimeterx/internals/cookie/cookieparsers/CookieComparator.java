package com.perimeterx.internals.cookie.cookieparsers;

import com.perimeterx.internals.cookie.RawCookieData;

import java.util.Comparator;

public class CookieComparator implements Comparator<RawCookieData> {
    @Override
    public int compare(RawCookieData rawCookieData, RawCookieData rawCookieDataOther) {

        return rawCookieDataOther.getCookieVersion().getVersionLevel() - rawCookieData.getCookieVersion().getVersionLevel();
    }
}
