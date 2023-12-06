package com.perimeterx.internals.cookie.cookieparsers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.perimeterx.api.PerimeterX;
import com.perimeterx.internals.cookie.CookieVersion;
import com.perimeterx.internals.cookie.DataEnrichmentCookie;
import com.perimeterx.internals.cookie.RawCookieData;
import com.perimeterx.utils.Base64;
import com.perimeterx.utils.Constants;
import com.perimeterx.utils.HMACUtils;
import com.perimeterx.utils.logger.IPXLogger;
import com.perimeterx.utils.logger.LogReason;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public abstract class HeaderParser {

    protected static final IPXLogger logger = PerimeterX.globalLogger;

    protected abstract String[] splitHeader(String header);

    protected abstract RawCookieData createCookie(String cookie);

    /**
     * This function receives a cookie from the cookie http header, parses it and returns all the available px cookies in a linked list.
     *
     * @param cookieHeaders Should contain the cookie(or cookies) that needs to be parsed into RawCookieData, can be null or empty
     * @return All px cookies available from the header.
     */
    public List<RawCookieData> createRawCookieDataList(String... cookieHeaders) {
        return Stream.of(cookieHeaders)
                .filter(StringUtils::isNoneEmpty)
                .map(this::splitHeader)
                .flatMap(Stream::of)
                .map(this::createCookie)
                .filter(Objects::nonNull)
                .sorted()
                .collect(toList());
    }

    public DataEnrichmentCookie getRawDataEnrichmentCookie(List<RawCookieData> rawCookies, String cookieKey) {
        ObjectMapper mapper = new ObjectMapper();
        DataEnrichmentCookie dataEnrichmentCookie = new DataEnrichmentCookie(mapper.createObjectNode(), false);
        RawCookieData rawDataEnrichmentCookie = null;
        for (RawCookieData rawCookie : rawCookies) {
            if (CookieVersion.DATA_ENRICHMENT.equals(rawCookie.getCookieVersion())) {
                rawDataEnrichmentCookie = rawCookie;
                break;
            }
        }

        if (rawDataEnrichmentCookie != null) {
            String[] cookiePayloadArray = rawDataEnrichmentCookie.getSelectedCookie().split(":");
            if (cookiePayloadArray.length != 2) {
                return dataEnrichmentCookie;
            }

            String hmac = cookiePayloadArray[0];
            String encodedPayload = cookiePayloadArray[1];

            boolean isValid = HMACUtils.isHMACValid(encodedPayload, hmac, cookieKey, logger);
            dataEnrichmentCookie.setValid(isValid);

            byte[] decodedPayload = Base64.decode(encodedPayload);
            try {
                dataEnrichmentCookie.setJsonPayload(mapper.readTree(decodedPayload));
            } catch (IOException e) {
                logger.error(LogReason.ERROR_DATA_ENRICHMENT_JSON_PARSING_FAILED);
            }
        }

        return dataEnrichmentCookie;
    }

    protected static CookieVersion getCookieVersion(String version) {
        switch (version) {
            case "3":
                return CookieVersion.V3;
            case "1":
                return CookieVersion.V1;
            case Constants.COOKIE_V1_KEY:
                return CookieVersion.V1;
            case Constants.COOKIE_V3_KEY:
                return CookieVersion.V3;
            case Constants.DATA_ENRICHMENT:
                return CookieVersion.DATA_ENRICHMENT;
            default:
                return CookieVersion.UNDEFINED;
        }
    }

    protected static boolean isValidPxCookie(String version) {
        return "3".equals(version) || "1".equals(version) || Constants.COOKIE_V3_KEY.equals(version) ||
                Constants.COOKIE_V1_KEY.equals(version) || Constants.DATA_ENRICHMENT.equals(version);
    }
}
