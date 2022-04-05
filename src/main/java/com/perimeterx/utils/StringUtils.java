package com.perimeterx.utils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static com.perimeterx.utils.Constants.*;
import static com.perimeterx.utils.Constants.UNICODE_TYPE;
import static com.perimeterx.utils.HashAlgorithm.SHA256;

public final class StringUtils {
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    private final static int HEX_BASE = 16;
    private final static int GENERATED_HASH_LENGTH = 32;
    private final static String OWASP_EMAIL_ADDRESS_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private final static String GMAIL_DOMAIN = "@gmail.com";

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
        } catch (NoSuchAlgorithmException abc) {
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

    public static boolean isValid(String email) {
        final Pattern pat = Pattern.compile(OWASP_EMAIL_ADDRESS_REGEX);

        if (email == null) {
            return false;
        }
        return pat.matcher(email).matches();
    }

    public static String getV2NormalizedEmailAddress(String emailAddress) {
        final String lowercaseAddress = emailAddress.toLowerCase();
        final int index = lowercaseAddress.indexOf('@');
        final String domain = lowercaseAddress.substring(index);

        String username = lowercaseAddress.substring(0,index);
        username = username.replace(".", "");

        if (domain.equals(GMAIL_DOMAIN)) {
            final int plusIndex = username.indexOf("+");
            username = plusIndex != -1 ? username.substring(0, plusIndex) : username;
        }

        return username + domain;
    }

    public static String getEncodedV2Password(String normalizedUsername, String password) {
        final String encodedUserName = encodeString(normalizedUsername, SHA256);
        final String encodedPassword = encodeString(password, SHA256);

        return encodeString(encodedUserName + encodedPassword, SHA256);
    }
}