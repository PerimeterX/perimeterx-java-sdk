package com.perimeterx.internals.cookie;

import com.perimeterx.utils.Constants;
import lombok.Getter;

@Getter
public enum CookieVersion {

    UNDEFINED("UNDEFINED",0),
    _V1(Constants.COOKIE_V1_KEY,1),
    _V3(Constants.COOKIE_V3_KEY,3);

    private String versionName;

    private int versionLevel;

    CookieVersion (String versionName, int versionLevel){
        this.versionLevel = versionLevel;
        this.versionName = versionName;
    }

}
