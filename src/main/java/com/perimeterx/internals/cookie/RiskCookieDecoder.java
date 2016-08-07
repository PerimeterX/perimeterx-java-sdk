package com.perimeterx.internals.cookie;

import com.fasterxml.jackson.databind.ObjectReader;
import com.perimeterx.utils.JsonUtils;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;

public class RiskCookieDecoder {

    public static final String HMAC = "HmacSHA256";
    public static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    public static final int KEY_LEN = 32;
    private final Cipher cipher;
    private final Mac mac;
    private final ObjectReader jsonReader;
    private final char[] password;
    private SecretKeyFactory secretKeyFactory;

    public enum ValidationResult {VALID, NO_SIGNING, EXPIRED, INVALID}

    public RiskCookieDecoder(String cookieKey) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException {
        // cookie key into secret
        password = cookieKey.toCharArray();
        // aes-256-cbc decryptData no salt
        cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "BC");
        // mac digest
        mac = Mac.getInstance(HMAC);
        SecretKeySpec macKey = new SecretKeySpec(cookieKey.getBytes(StandardCharsets.UTF_8), HMAC);
        mac.init(macKey);
        // json parser
        this.jsonReader = JsonUtils.riskCookieReader;
        secretKeyFactory = SecretKeyFactory.getInstance(ALGORITHM);
    }

    private String decryptData(String cookieData) throws InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeySpecException {
        final String[] parts = cookieData.split(":");
        final byte[] salt = Base64.getDecoder().decode(parts[0].getBytes(StandardCharsets.UTF_8));
        final int iterations = Integer.parseInt(parts[1]);
        final byte[] encrypted = Base64.getDecoder().decode(parts[2].getBytes(StandardCharsets.UTF_8));
        final int dkLen = (KEY_LEN + cipher.getBlockSize()) * 8;
        PBEKeySpec keySpec = new PBEKeySpec(password, salt, iterations, dkLen);
        SecretKey dk = secretKeyFactory.generateSecret(keySpec);

        byte[] encoded = dk.getEncoded();
        byte[] key = Arrays.copyOf(encoded, KEY_LEN);
        byte[] iv = Arrays.copyOfRange(encoded, KEY_LEN, encoded.length);

        SecretKey secretKey = new SecretKeySpec(key, "AES");
        IvParameterSpec parameterSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);
        final byte[] data = cipher.doFinal(encrypted, 0, encrypted.length);
        return new String(data, StandardCharsets.UTF_8);
    }

    public RiskCookie decryptRiskCookie(String cookieData) throws BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeySpecException, IOException {
        // decrypt and parse
        final String decryptData = decryptData(cookieData);
        return jsonReader.readValue(decryptData);
    }

    public boolean isValid(RiskCookie riskCookie, String[] signingFields) {
        return validate(riskCookie, signingFields) == ValidationResult.VALID;
    }

    public ValidationResult validate(RiskCookie riskCookie, String[] signingFields) {
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

    private byte[] digest(RiskCookie riskCookie, String[] signingFields) {
        final String data = asSigningData(riskCookie, signingFields);
        mac.reset();
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
