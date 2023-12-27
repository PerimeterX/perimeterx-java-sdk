package com.perimeterx.internals.cookie;

import com.fasterxml.jackson.databind.JsonNode;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.configuration.PXConfiguration;

/**
 * Created by nitzangoldfeder on 13/04/2017.
 */
public class PXCookieV1 extends AbstractPXCookie {

    public PXCookieV1(PXConfiguration pxConfiguration, CookieData cookieData, PXContext context) {
        super(pxConfiguration, cookieData, context);
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
    public boolean isSecured() {
        String baseHmacStr = new StringBuilder()
                .append(this.getTimestamp())
                .append(this.getDecodedCookie().get("s").get("a").asInt())
                .append(this.getScore())
                .append(this.getUUID())
                .append(this.getVID())
                .toString();
        String hmacWithIp = new StringBuilder()
                .append(baseHmacStr)
                .append(ip)
                .append(userAgent)
                .toString();
        String hmacWithoutIp = new StringBuilder()
                .append(baseHmacStr)
                .append(userAgent)
                .toString();

        return this.isHmacValid(hmacWithIp, this.getHmac()) || this.isHmacValid(hmacWithoutIp, this.getHmac());
    }
}
