package com.perimeterx.utils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import static com.perimeterx.utils.Constants.*;
import static com.perimeterx.utils.Constants.UNICODE_TYPE;

public final class StringUtils {
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    private final static int HEX_BASE = 16;
    private final static int GENERATED_HASH_LENGTH = 32;

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            if (s.charAt(i) == ' ') continue;
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static String byteArrayToHexString(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String encodeString(String text, HashAlgorithm hashAlgorithm) {
        try {
            final MessageDigest msgDst = MessageDigest.getInstance(hashAlgorithm.getValue());
            final byte[] msgArr = msgDst.digest(text.getBytes());
            final BigInteger bi = new BigInteger(1, msgArr);

            StringBuilder hshtxt = new StringBuilder(bi.toString(HEX_BASE));

            while (hshtxt.length() < GENERATED_HASH_LENGTH) {
                hshtxt.insert(0, "0");
            }

            return hshtxt.toString();
        }
        catch (NoSuchAlgorithmException abc) {
            throw new RuntimeException(abc);
        }
    }

    public static Map<String, String> splitQueryParams(String queryParams) throws UnsupportedEncodingException {
        final Map<String, String> params = new HashMap<>();
        final String[] pairs = queryParams.split(QUERY_PARAM_PAIRS_SEPARATOR);
        int idx;

        for (String pair : pairs) {
            idx = pair.indexOf(QUERY_PARAM_KEY_VALUE_SEPARATOR);
            params.put(URLDecoder.decode(pair.substring(0, idx), UNICODE_TYPE), URLDecoder.decode(pair.substring(idx + 1), UNICODE_TYPE));
        }

        return params;
    }
}