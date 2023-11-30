package com.perimeterx.utils;

import com.perimeterx.utils.logger.PXLogger;
import org.testng.annotations.Test;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.*;

public class HMACUtilsTest {

    private String baseString = "hello-from-ilai";
    private String hmac = "8F85769F65278C96695CE9C61B3FDD7C8991CB04F4C42D2A66E5C56FBCA6FDCC";
    private String cookieKey = "ilai's-cookie-dont-touch";
    private PXLogger logger = PXLogger.getLogger(HMACUtilsTest.class);

    @Test
    public void testHMACString() throws InvalidKeyException, NoSuchAlgorithmException {
        byte[] actualHmac = HMACUtils.HMACString(baseString, cookieKey);
        String actualHmacHexString = StringUtils.byteArrayToHexString(actualHmac);
        assertEquals(hmac, actualHmacHexString);
    }

    @Test
    public void testIsHMACValid() {
        boolean isValid = HMACUtils.isHMACValid(baseString, hmac, cookieKey, logger);
        assertTrue(isValid);

        isValid = HMACUtils.isHMACValid(baseString + "-something-else", hmac, cookieKey, logger);
        assertFalse(isValid);
    }
}