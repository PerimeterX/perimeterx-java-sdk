package testutils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.perimeterx.utils.HMACUtils;
import com.perimeterx.utils.PBKDF2Engine;
import com.perimeterx.utils.PBKDF2Parameters;
import com.perimeterx.utils.StringUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.SneakyThrows;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;


@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
public final class CookieV3Generator {

    private String cookieSecret;
    @Builder.Default
    private int iterations = 1000;
    @Builder.Default
    private LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(5);
    @Builder.Default
    private byte[] salt = randomBytes(16);
    @Builder.Default
    private String userAgent = "test_user_agent";
    @Builder.Default
    private int keyLen = 32;
    @Builder.Default
    private String uuid = UUID.randomUUID().toString();
    @Builder.Default
    private String vid = UUID.randomUUID().toString();
    @Builder.Default
    private String action = "c";
    @Builder.Default
    private int score = 0;
    @Builder.Default
    private String cookieSigningFields = "u";
    @Builder.Default
    private String ip = "";

    public static CookieV3GeneratorBuilder builder(String cookieSecret) {
        return new CookieV3GeneratorBuilder().cookieSecret(cookieSecret);
    }

    @Override
    @SneakyThrows
    public String toString() {
        final Cipher cipher;   // aes-256-cbc decryptData no salt
        cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        final int dkLen = this.keyLen + cipher.getBlockSize();
        PBKDF2Parameters p = new PBKDF2Parameters("HmacSHA256", "UTF-8", this.salt, this.iterations);
        byte[] plain = plain().toString().getBytes();
        byte[] dk = new PBKDF2Engine(p).deriveKey(this.cookieSecret, dkLen);
        byte[] key = Arrays.copyOf(dk, this.keyLen);
        byte[] iv = Arrays.copyOfRange(dk, this.keyLen, dk.length);
        SecretKey secretKey = new SecretKeySpec(key, "AES");
        IvParameterSpec parameterSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);
        final byte[] cipherData = cipher.doFinal(plain, 0, plain.length);
        return buildCookie(cipherData);
    }

    public JsonNode plain() {
        ObjectNode json = new ObjectMapper().createObjectNode();
        json.put("t", this.expiryDate.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000);
        json.put("u", this.uuid);
        json.put("v", this.vid);
        json.put("a", this.action);
        json.put("s", this.score);
        json.put("x", this.cookieSigningFields);
        return json;
    }

    @SneakyThrows
    private String buildCookie(byte[] cipherData) {
        Base64.Encoder b64Encoder = Base64.getEncoder();
        String content = b64Encoder.encodeToString(this.salt) +
                ":" +
                this.iterations +
                ":" +
                b64Encoder.encodeToString(cipherData);

        String hmac = StringUtils.byteArrayToHexString(
                HMACUtils.HMACString(content + hmacAdditionalData(), this.cookieSecret));

        return "_px3=" + hmac + ":" + content;
    }

    private String hmacAdditionalData() {
        return this.cookieSigningFields.replaceAll("s", this.ip).replace("u", this.userAgent);
    }

    @SneakyThrows
    private static byte[] randomBytes(int length) {
        byte[] bytes = new byte[length];
        SecureRandom.getInstanceStrong().nextBytes(bytes);
        return bytes;
    }
}
