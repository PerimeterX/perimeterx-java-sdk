package com.perimeterx.api.additionalContext.credentialsIntelligence.loginresponse;

import com.perimeterx.api.verificationhandler.IsSensitivePredicate;

import javax.servlet.http.HttpServletRequest;

public class DefaultIsSensitiveRequestPredicate implements IsSensitivePredicate {
    @Override
    public boolean test(HttpServletRequest iRequestWrapper) {
        return false;
    }
}
