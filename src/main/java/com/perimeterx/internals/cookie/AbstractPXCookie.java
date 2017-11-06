package com.perimeterx.internals.cookie;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.exceptions.PXCookieDecryptionException;
import com.perimeterx.models.exceptions.PXException;
import com.perimeterx.utils.Base64;
import com.perimeterx.utils.PBKDF2Engine;
import com.perimeterx.utils.PBKDF2Parameters;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Created by nitzangoldfeder on 13/04/2017.
 */
public abstract class AbstractPXCookie implements PXCookie {

    private static final int KEY_LEN = 32;
    private static final String HMAC_SHA_256 = "HmacSHA256";

    protected ObjectMapper mapper;
    protected PXConfiguration pxConfiguration;
    protected PXContext pxContext;
    protected String pxCookie;
    protected JsonNode decodedCookie;
    protected String cookieKey;

    public AbstractPXCookie(PXConfiguration pxConfiguration, PXContext pxContext) {
        this.mapper = new ObjectMapper();
        this.pxConfiguration = pxConfiguration;
        this.pxContext = pxContext;
        this.pxCookie = pxContext.getPxCookie();
        this.cookieKey = pxConfiguration.getCookieKey();
    }

    public String getPxCookie() {
        return pxCookie;
    }

    public void setPxCookie(String pxCookie) {
        this.pxCookie = pxCookie;
    }

    public JsonNode getDecodedCookie() {
        return decodedCookie;
    }

    public void setDecodedCookie(JsonNode decodedCookie) {
        this.decodedCookie = decodedCookie;
    }

    public boolean deserialize() throws PXCookieDecryptionException {

        if (this.decodedCookie != null) {
            return true;
        }

        JsonNode decodedCookie = null;
        if (this.pxConfiguration.isEncryptionEnabled()) {
            decodedCookie = this.decrypt();
        } else {
            decodedCookie = this.decode();
        }

        if (!isCookieFormatValid(decodedCookie)) {
            return false;
        }

        this.decodedCookie = decodedCookie;
        return true;
    }

    private JsonNode decrypt() throws PXCookieDecryptionException {
        final String[] parts = this.pxCookie.split(":");
        if (parts.length != 3) {
            throw new PXCookieDecryptionException("Part length invalid");
        }
        final byte[] salt = Base64.decode(parts[0]);
        if (salt == null) {
            throw new PXCookieDecryptionException("Salt is empty");
        }
        final int iterations = Integer.parseInt(parts[1]);
        if (iterations < 0 || iterations > 10000) {
            throw new PXCookieDecryptionException("Iterations not in range");
        }
        final byte[] encrypted = Base64.decode(parts[2]);
        if (encrypted == null) {
            throw new PXCookieDecryptionException("No payload");
        }

        final Cipher cipher;   // aes-256-cbc decryptData no salt
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            final int dkLen = KEY_LEN + cipher.getBlockSize();
            PBKDF2Parameters p = new PBKDF2Parameters(HMAC_SHA_256, "UTF-8", salt, iterations);
            byte[] dk = new PBKDF2Engine(p).deriveKey(this.cookieKey, dkLen);
            byte[] key = Arrays.copyOf(dk, KEY_LEN);
            byte[] iv = Arrays.copyOfRange(dk, KEY_LEN, dk.length);
            SecretKey secretKey = new SecretKeySpec(key, "AES");
            IvParameterSpec parameterSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);
            final byte[] data = cipher.doFinal(encrypted, 0, encrypted.length);

            String decryptedString = new String(data, StandardCharsets.UTF_8);
            return mapper.readTree(decryptedString);
        } catch (Exception e) {
            throw new PXCookieDecryptionException("Cookie decryption failed in reason => ".concat(e.getMessage()));
        }
    }

    private JsonNode decode() throws PXCookieDecryptionException {
        try {
            byte[] decodedBytes = Base64.decode(this.pxCookie);
            String decodedString = new String(decodedBytes);
            return mapper.readTree(decodedString);
        } catch (IOException e) {
            throw new PXCookieDecryptionException("Cookie decode failed in reason => ".concat(e.getMessage()));
        }
    }

    public boolean isHighScore() {
        return this.getScore() >= this.pxConfiguration.getBlockingScore();
    }

    public boolean isExpired() {
        return this.getTimestamp() < System.currentTimeMillis();
    }

    public boolean isHmacValid(String hmacStr, String cookieHmac) throws PXException {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(this.cookieKey.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] b_hmac = sha256_HMAC.doFinal(hmacStr.getBytes(StandardCharsets.UTF_8));
            byte[] b_cookieHmac = hexStringToByteArray(cookieHmac);

            return Arrays.equals(b_hmac, b_cookieHmac);
        } catch (Exception e) {
            throw new PXException("Failed to validate HMAC => ".concat(e.getMessage()));
        }
    }

    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            if (s.charAt(i) == ' ') continue;
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public boolean isValid() throws PXCookieDecryptionException, PXException {
        return this.deserialize() && !this.isExpired() && this.isSecured();
    }

    @Override
    public long getTimestamp() {
        return decodedCookie.get("t").asLong();
    }

    @Override
    public String getUUID() {
        return decodedCookie.get("u").asText();
    }

    @Override
    public String getVID() {
        return decodedCookie.get("v").asText();
    }

}
