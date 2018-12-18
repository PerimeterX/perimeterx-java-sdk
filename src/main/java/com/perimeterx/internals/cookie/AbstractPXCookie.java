package com.perimeterx.internals.cookie;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.exceptions.PXCookieDecryptionException;
import com.perimeterx.utils.*;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Created by nitzangoldfeder on 13/04/2017.
 */
public abstract class AbstractPXCookie implements PXCookie {

    private static final PXLogger logger = PXLogger.getLogger(AbstractPXCookie.class);

    private static final int KEY_LEN = 32;
    private static final String HMAC_SHA_256 = "HmacSHA256";

    private String cookieVersion;
    protected String ip;

    protected String userAgent;

    protected ObjectMapper mapper;
    protected PXConfiguration pxConfiguration;
    protected String pxCookie;
    protected JsonNode decodedCookie;
    protected String cookieKey;
    protected String cookieOrig;

    public AbstractPXCookie(PXConfiguration pxConfiguration, CookieData cookieData) {
        this.mapper = new ObjectMapper();
        this.pxCookie = cookieData.getPxCookie();
        this.cookieOrig = cookieData.getPxCookie();
        this.pxConfiguration = pxConfiguration;
        this.userAgent = cookieData.isMobileToken() ? "" : cookieData.getUserAgent();
        this.ip = cookieData.getIp();
        this.cookieKey = pxConfiguration.getCookieKey();
        this.cookieVersion = cookieData.getCookieVersion();
    }

    public String getPxCookie() {
        return pxCookie;
    }

    public String getCookieOrig() {
        return cookieOrig;
    }

    public String getCookieVersion() {
        return cookieVersion;
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

        JsonNode decodedCookie;
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

    public boolean isHmacValid(String hmacStr, String cookieHmac) {
        boolean isValid = HMACUtils.isHMACValid(hmacStr, cookieHmac, this.cookieKey, logger);
        if (!isValid) {
            logger.debug(PXLogger.LogReason.DEBUG_COOKIE_DECRYPTION_HMAC_FAILED, pxCookie, this.userAgent);
        }

        return isValid;
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
