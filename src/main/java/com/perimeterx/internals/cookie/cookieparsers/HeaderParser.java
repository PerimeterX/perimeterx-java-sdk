package com.perimeterx.internals.cookie.cookieparsers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.perimeterx.internals.cookie.CookieVersion;
import com.perimeterx.internals.cookie.DataEnrichmentCookie;
import com.perimeterx.internals.cookie.RawCookieData;
import com.perimeterx.utils.Base64;
import com.perimeterx.utils.Constants;
import com.perimeterx.utils.HMACUtils;
import com.perimeterx.utils.PXLogger;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class HeaderParser {

    protected static final PXLogger logger = PXLogger.getLogger(HeaderParser.class);

    protected abstract String[] splitHeader(String header);

    protected abstract RawCookieData createCookie(String cookie);

    /**
     * This function receives a cookie from the cookie http header, parses it and returns all the available px cookies in a linked list.
     *
     * @param cookieHeader Should contain the cookie(or cookies) that needs to be parsed into RawCookieData, can be null or empty
     * @return All px cookies available from the header.
     */
    public List<RawCookieData> createRawCookieDataList(String cookieHeader) {
        List<RawCookieData> cookieList = new ArrayList<>();
        if (!StringUtils.isEmpty(cookieHeader)) {
            String[] cookies = splitHeader(cookieHeader);
            for (String cookie : cookies) {
                RawCookieData rawCookie = createCookie(cookie);
                if (rawCookie != null) {
                    cookieList.add(rawCookie);
                }
            }
        }
        Collections.sort(cookieList);
        return cookieList;
    }

    public DataEnrichmentCookie getRawDataEnrichmentCookie(List<RawCookieData> rawCookies, String cookieKey) {
        DataEnrichmentCookie dataEnrichmentCookie = null;
        RawCookieData dataEnrichmentRawCookie = null;
        for (RawCookieData rawCookie : rawCookies) {
            if (CookieVersion.DATA_ENRICHMENT.equals(rawCookie.getCookieVersion())) {
                dataEnrichmentRawCookie = rawCookie;
                break;
            }
        }

        if (dataEnrichmentRawCookie != null) {
            String[] cookiePayloadArray = dataEnrichmentRawCookie.getSelectedCookie().split(":");
            if (cookiePayloadArray.length != 2) {
                return null;
            }

            String hmac = cookiePayloadArray[0];
            String encodedPayload = cookiePayloadArray[1];

            boolean isValid = HMACUtils.isHMACValid(encodedPayload, hmac, cookieKey, logger);
            if (!isValid) {
                return null;
            }

            byte[] decodedPayload = Base64.decode(encodedPayload);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode cookieJsonPayload = null;
            try {
                cookieJsonPayload = mapper.readTree(decodedPayload);
            } catch (IOException e) {
                logger.error(PXLogger.LogReason.ERROR_DATA_ENRICHMENT_JSON_PARSING_FAILED);
            }

            dataEnrichmentCookie = new DataEnrichmentCookie(cookieJsonPayload);
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
