package com.perimeterx.internals.cookie;

import com.fasterxml.jackson.databind.JsonNode;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.exceptions.PXException;

/**
 * Created by nitzangoldfeder on 13/04/2017.
 */
public class PXCookieV3 extends AbstractPXCookie {

    private String hmac;

    public PXCookieV3(PXConfiguration pxConfiguration, PXContext pxContext) {
        super(pxConfiguration, pxContext);
        String[] splicedCookie = getPxCookie().split(":", 2);
        if (splicedCookie.length > 1) {
            this.pxCookie = splicedCookie[1];
            this.hmac = splicedCookie[0];
        }
    }

    @Override
    public String getHmac() {
        return this.hmac;
    }

    @Override
    public String getBlockAction() {
        return this.decodedCookie.get("a").asText();
    }

    @Override
    public int getScore() {
        return decodedCookie.get("s").asInt();
    }

    @Override
    public boolean isCookieFormatValid(JsonNode decodedCookie) {
        return decodedCookie.has("t") &&
                decodedCookie.has("s") &&
                decodedCookie.has("u") &&
                decodedCookie.has("v") &&
                decodedCookie.has("a");
    }

    @Override
    public boolean isSecured() throws PXException {
        String hmacString = new StringBuilder()
                .append(this.getPxCookie())
                .append(this.pxContext.getUserAgent())
                .toString();
        return this.isHmacValid(hmacString, this.getHmac());
    }
}
