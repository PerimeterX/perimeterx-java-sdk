package com.perimeterx.internals.cookie;

import com.perimeterx.utils.Constants;
import lombok.Getter;

@Getter
public enum CookieVersion {
    UNDEFINED("UNDEFINED", 0),
    V1(Constants.COOKIE_V1_KEY, 1),
    V3(Constants.COOKIE_V3_KEY, 3),
    DATA_ENRICHMENT(Constants.DATA_ENRICHMENT, -1);

    private String versionName;

    private int versionLevel;

    CookieVersion(String versionName, int versionLevel) {
        this.versionLevel = versionLevel;
        this.versionName = versionName;
    }
}
