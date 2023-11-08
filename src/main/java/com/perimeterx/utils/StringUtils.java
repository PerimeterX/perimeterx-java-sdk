package com.perimeterx.utils;

import com.perimeterx.models.configuration.credentialsIntelligenceconfig.ConfigCredentialsFieldPath;
import com.perimeterx.models.configuration.credentialsIntelligenceconfig.LoginCredentials;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.perimeterx.utils.Constants.*;

public final class StringUtils {
    private final static Pattern MULTIPART_PATTERN = Pattern.compile("name=\"([^\"]+)\"\\s*(?:\n\\s*\\n)*\\s*\\s*\\s*\\s*\\s*(.*?)\\s*(?:\n|$)", Pattern.DOTALL);

    private final static char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    private final static int HEX_BASE = 16;
    private final static int GENERATED_HASH_LENGTH_LIMIT = 64;
    final static Pattern OWASP_EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");

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
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String encodeString(String text, HashAlgorithm hashAlgorithm) {
        try {
            final MessageDigest msgDst = MessageDigest.getInstance(hashAlgorithm.getValue());
            final byte[] msgArr = msgDst.digest(text.getBytes());
            final BigInteger bi = new BigInteger(1, msgArr);

            StringBuilder hshtxt = new StringBuilder(bi.toString(HEX_BASE));

            while (hshtxt.length() < GENERATED_HASH_LENGTH_LIMIT) {
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
        if (email == null) {
            return false;
        }
        return OWASP_EMAIL_PATTERN.matcher(email).matches();
    }

    public static LoginCredentials extractCredentialsFromMultipart(String formData, ConfigCredentialsFieldPath credentialsFieldPath) {
        final Map<String, String> data = extractFieldsFromMultipart(formData);

        return new LoginCredentials(
                data.get(credentialsFieldPath.getUsernameFieldPath()),
                data.get(credentialsFieldPath.getPasswordFieldPath()));
    }

    private static Map<String, String> extractFieldsFromMultipart(String body) {
        final Map<String, String> fieldMap = new HashMap<>();
        final Matcher matcher = MULTIPART_PATTERN.matcher(body);

        while (matcher.find()) {
            String fieldName = matcher.group(1);
            String fieldValue = matcher.group(2).trim();

            fieldMap.put(fieldName, fieldValue);
        }

        return fieldMap;
    }
}