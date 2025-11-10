package com.perimeterx.internals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.configuration.PXConfiguration;

import javax.servlet.http.Cookie;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public final class JwtUserIdentifiersExtractor {
    private static final ObjectMapper OM = new ObjectMapper();

    private JwtUserIdentifiersExtractor() {}

    public static void attachJwtIfConfigured(PXContext ctx, PXConfiguration cfg) {
        tryExtractFromCookie(ctx, cfg);
        if (ctx.getJwtAppUserId() != null || (ctx.getJwtAdditionalFields() != null && !ctx.getJwtAdditionalFields().isEmpty())) {
            return;
        }
        tryExtractFromHeader(ctx, cfg);
    }

    private static void tryExtractFromCookie(PXContext ctx, PXConfiguration cfg) {
        String cookieName = nullToEmpty(cfg.getPxJwtCookieName());
        if (cookieName.isEmpty()) return;
        String token = findCookieValue(ctx, cookieName);
        buildAndSet(ctx, token, cfg.getPxJwtCookieUserIdFieldName(), safeList(cfg.getPxJwtCookieAdditionalFieldNames()));
    }

    private static void tryExtractFromHeader(PXContext ctx, PXConfiguration cfg) {
        String headerName = nullToEmpty(cfg.getPxJwtHeaderName());
        if (headerName.isEmpty()) return;
        String raw = ctx.getRequest().getHeader(headerName);
        if (raw == null) return;
        String token = raw.startsWith("Bearer ") ? raw.substring(7).trim() : raw.trim();
        buildAndSet(ctx, token, cfg.getPxJwtHeaderUserIdFieldName(), safeList(cfg.getPxJwtHeaderAdditionalFieldNames()));
    }

    private static void buildAndSet(PXContext ctx, String token, String userPath, List<String> additionalPaths) {
        if (isEmpty(token)) return;
        try {
            Map<String, Object> payload = decodePayload(token);
            String appUserId = asString(getByDotPath(payload, userPath));
            Map<String, Object> additional = collectByDotPaths(payload, additionalPaths);
            if (isEmpty(appUserId) && additional.isEmpty()) return;
            ctx.setJwtAppUserId(appUserId);
            ctx.setJwtAdditionalFields(additional.isEmpty() ? null : additional);
        } catch (Exception e) {
            ctx.logger.debug("JWT extraction skipped: invalid token", e);
        }
    }

    private static Map<String, Object> decodePayload(String jwt) throws Exception {
        String[] parts = jwt.split("\\.");
        if (parts.length < 2) throw new IllegalArgumentException("Invalid JWT");
        byte[] json = Base64.getUrlDecoder().decode(parts[1]);
        return OM.readValue(json, Map.class);
    }

    private static String findCookieValue(PXContext ctx, String name) {
        Cookie[] cookies = ctx.getRequest().getCookies();
        if (cookies == null) return null;
        for (Cookie c : cookies) {
            if (name.equals(c.getName())) {
                String v = c.getValue();
                try {
                    return v != null ? URLDecoder.decode(v, StandardCharsets.UTF_8.toString()) : null;
                } catch (UnsupportedEncodingException e) {
                    return v;
                }
            }
        }
        return null;
    }

    private static Object getByDotPath(Map<String, Object> json, String path) {
        if (json == null || isEmpty(path)) return null;
        Object cur = json;
        for (String seg : path.split("\\.")) {
            if (!(cur instanceof Map)) return null;
            cur = ((Map<?, ?>) cur).get(seg);
            if (cur == null) return null;
        }
        return cur;
    }

    private static Map<String, Object> collectByDotPaths(Map<String, Object> json, List<String> paths) {
        Map<String, Object> out = new LinkedHashMap<>();
        for (String p : paths) {
            Object v = getByDotPath(json, p);
            if (v != null) out.put(getLastSegment(p), v);
        }
        return out;
    }

    private static String getLastSegment(String path) {
        if (isEmpty(path)) return path;
        int lastDot = path.lastIndexOf('.');
        return lastDot >= 0 ? path.substring(lastDot + 1) : path;
    }

    private static List<String> safeList(List<String> l) { return l == null ? Collections.emptyList() : l; }
    private static boolean isEmpty(String s) { return s == null || s.isEmpty(); }
    private static String nullToEmpty(String s) { return s == null ? "" : s; }
    private static String asString(Object v) { return v == null ? null : String.valueOf(v); }
}


