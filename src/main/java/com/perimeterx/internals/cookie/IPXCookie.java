package com.perimeterx.internals.cookie;

import com.fasterxml.jackson.databind.JsonNode;
import com.perimeterx.models.exceptions.PXException;

/**
 * Created by nitzangoldfeder on 13/04/2017.
 */
public interface IPXCookie {
    String getHmac();
    String getUUID();
    String getVID();
    String getBlockAction();
    long getTimestamp();
    int getScore();

    boolean isCookieFormatValid(JsonNode decodedCookie);
    boolean isSecured() throws PXException;

}
