package com.perimeterx.api.providers;

import javax.servlet.http.HttpServletRequest;

public class DefaultIsSensitivePredicate implements IsSensitivePredicate {
    @Override
    public boolean test(HttpServletRequest httpServletRequest) {
        return false;
    }
}
