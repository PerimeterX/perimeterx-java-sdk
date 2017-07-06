package com.perimeterx.internals.cookie;

import com.fasterxml.jackson.databind.JsonNode;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.exceptions.PXException;

/**
 * Created by nitzangoldfeder on 13/04/2017.
 */
public class PXCookieV1 extends PXCookie {

    public PXCookieV1(PXConfiguration pxConfiguration, PXContext pxContext) {
        super(pxConfiguration, pxContext);
    }

    @Override
    public String getHmac() {
        return this.getDecodedCookie().get("h").asText();
    }

    @Override
    public String getBlockAction() {
        return "c";
    }

    @Override
    public int getScore() {
        return this.getDecodedCookie().get("s").get("b").asInt();
    }

    @Override
    public boolean isCookieFormatValid(JsonNode decodedCookie) {
        return decodedCookie.has("t") &&
                decodedCookie.has("s") &&
                decodedCookie.get("s").has("b") &&
                decodedCookie.has("u") &&
                decodedCookie.has("v") &&
                decodedCookie.has("h");
    }

    @Override
    public boolean isSecured() throws PXException {
        String baseHmacStr = new StringBuilder()
                .append(this.getTimestamp())
                .append(this.getDecodedCookie().get("s").get("a").asInt())
                .append(this.getScore())
                .append(this.getUUID())
                .append(this.getVID())
                .toString();
        String hmacWithIp = new StringBuilder()
                .append(baseHmacStr.toString())
                .append(this.pxContext.getIp())
                .append(this.pxContext.getUserAgent())
                .toString();
        String hmacWithoutIp = new StringBuilder()
                .append(baseHmacStr)
                .append(this.pxContext.getUserAgent())
                .toString();

        return this.isHmacValid(hmacWithIp, this.getHmac()) || this.isHmacValid(hmacWithoutIp, this.getHmac());
    }
}
