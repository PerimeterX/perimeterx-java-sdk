package com.perimeterx.internals.cookie;

import com.perimeterx.utils.Base64;
import com.perimeterx.utils.JsonUtils;
import com.perimeterx.utils.PBKDF2Engine;
import com.perimeterx.utils.PBKDF2Parameters;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

public class RiskCookieDecoder {

    private static final int KEY_LEN = 32;
    private static final String HMAC_SHA_256 = "HmacSHA256";
    private final String cookieKey;

    public enum ValidationResult {VALID, NO_SIGNING, EXPIRED, INVALID}

    public RiskCookieDecoder(String cookieKey) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException, UnsupportedEncodingException {
        // cookie key into secret
        this.cookieKey = cookieKey;
    }

    private String decryptData(String cookieData) throws InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException {
        final String[] parts = cookieData.split(":");
        final byte[] salt = Base64.decode(parts[0]);
        if (salt == null) {
            return "";
        }
        final int iterations = Integer.parseInt(parts[1]);
        if (iterations < 0 || iterations > 10000) {
            return "";
        }
        final byte[] encrypted = Base64.decode(parts[2]);
        if (encrypted == null) {
            return "";
        }
        final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");   // aes-256-cbc decryptData no salt
        final int dkLen = KEY_LEN + cipher.getBlockSize();
        PBKDF2Parameters p = new PBKDF2Parameters(HMAC_SHA_256, "UTF-8", salt, iterations);
        byte[] dk = new PBKDF2Engine(p).deriveKey(this.cookieKey, dkLen);
        byte[] key = Arrays.copyOf(dk, KEY_LEN);
        byte[] iv = Arrays.copyOfRange(dk, KEY_LEN, dk.length);
        SecretKey secretKey = new SecretKeySpec(key, "AES");
        IvParameterSpec parameterSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);
        final byte[] data = cipher.doFinal(encrypted, 0, encrypted.length);
        return new String(data, StandardCharsets.UTF_8);
    }

    public RiskCookie decryptRiskCookie(String cookieData) throws BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeySpecException, IOException, NoSuchPaddingException {
        // decrypt and parse
        final String decryptData = decryptData(cookieData);
        return JsonUtils.riskCookieReader.readValue(decryptData);
    }

    public boolean isValid(RiskCookie riskCookie, String[] signingFields) throws NoSuchAlgorithmException, InvalidKeyException {
        return validate(riskCookie, signingFields) == ValidationResult.VALID;
    }

    public ValidationResult validate(RiskCookie riskCookie, String[] signingFields) throws InvalidKeyException, NoSuchAlgorithmException {
        // no hash
        if (riskCookie.hash == null || riskCookie.hash.length() == 0) {
            return ValidationResult.NO_SIGNING;
        }

        // expired
        long now = System.currentTimeMillis();
        if (now > riskCookie.timestamp) {
            return ValidationResult.EXPIRED;
        }

        // validate hash
        final byte[] cookieHash = hexStringToByteArray(riskCookie.hash);
        final byte[] hash = digest(riskCookie, signingFields);
        return Arrays.equals(hash, cookieHash) ? ValidationResult.VALID : ValidationResult.INVALID;
    }

    private byte[] digest(RiskCookie riskCookie, String[] signingFields) throws NoSuchAlgorithmException, InvalidKeyException {
        final String data = asSigningData(riskCookie, signingFields);
        final Mac mac = Mac.getInstance(HMAC_SHA_256);
        SecretKeySpec macKey = new SecretKeySpec(cookieKey.getBytes(StandardCharsets.UTF_8), HMAC_SHA_256);
        mac.init(macKey);
        mac.update(data.getBytes(StandardCharsets.UTF_8));
        return mac.doFinal();
    }

    private String asSigningData(RiskCookie riskCookie, String[] signingFields) {
        StringBuilder sb = new StringBuilder();
        sb.append(riskCookie.timestamp);

        if (riskCookie.score != null) {
            sb.append(riskCookie.score.application);
            sb.append(riskCookie.score.bot);
        }
        if (riskCookie.uuid != null) {
            sb.append(riskCookie.uuid);
        }
        if (riskCookie.vid != null) {
            sb.append(riskCookie.vid);
        }
        if (signingFields != null) {
            for (final String field : signingFields) {
                sb.append(field);
            }
        }
        return sb.toString();
    }

    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}
