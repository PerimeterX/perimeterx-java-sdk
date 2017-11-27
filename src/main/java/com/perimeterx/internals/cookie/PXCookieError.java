package com.perimeterx.internals.cookie;

import com.fasterxml.jackson.databind.JsonNode;
import com.perimeterx.models.exceptions.PXException;

public class PXCookieError extends AbstractPXCookie {

    private final String cookieError;

    public PXCookieError(String cookieError) {
        this.cookieError = cookieError;
    }

    @Override
    public String getHmac() {
        return null;
    }

    @Override
    public String getBlockAction() {
        return null;
    }

    @Override
    public int getScore() {
        return 0;
    }

    @Override
    public boolean isCookieFormatValid(JsonNode decodedCookie) {
        return false;
    }

    @Override
    public boolean isSecured() throws PXException {
        return false;
    }

    @Override
    public String getCookieError() {
        return cookieError;
    }
}
