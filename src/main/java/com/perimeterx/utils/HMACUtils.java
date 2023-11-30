package com.perimeterx.utils;

import com.perimeterx.utils.logger.PXLogger;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public final class HMACUtils {

    public static byte[] HMACString(String encodedString, String cookieKey)
            throws NoSuchAlgorithmException, InvalidKeyException {

        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(cookieKey.getBytes(), "HmacSHA256");
        sha256_HMAC.init(secretKey);
        return sha256_HMAC.doFinal(encodedString.getBytes(StandardCharsets.UTF_8));
    }

    public static boolean isHMACValid(String encodedString, String hmac,
                                      String cookieKey, PXLogger logger) {
        boolean isValid;
        try {
            byte[] bHMAC = HMACString(encodedString, cookieKey);
            byte[] bCookieHmac = StringUtils.hexStringToByteArray(hmac);
            isValid = Arrays.equals(bHMAC, bCookieHmac);
        } catch (Exception e) {
            logger.debug(PXLogger.LogReason.DEBUG_COOKIE_HMAC_VALIDATION_FAILED, e.getMessage());
            isValid = false;
        }

        return isValid;
    }
}