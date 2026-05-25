package com.web;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import org.json.JSONArray;
import org.json.JSONObject;

import jakarta.servlet.http.HttpServletRequest;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static com.web.Constants.*;

public class Utils {

    public static Set<String> jsonArrayToSet(JSONArray jsonArray) {
        Set<String> stringsSet = new HashSet<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            stringsSet.add(jsonArray.getString(i));
        }
        return stringsSet;
    }

    public static void setDefaultPageAttributes(HttpServletRequest request, Config config) {
        final String appId = getAppId();
        request.setAttribute(APP_ID_KEY, appId);
        request.setAttribute(SENSOR_SRC_KEY, "/" + getSensorSrc(appId, config));
    }

    private static String getAppId() {
        try {
            return getEnforcerConfig().getString(PX_APP_ID_FIELD);
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract App ID from enforcer config", e);
        }
    }

    private static String getSensorSrc(String appId, Config config) {
        if (config.getPxConfiguration().isFirstPartyEnabled()) {
            return appId.replace(PX_PREFIX, "") + FIRST_PARTY_SENSOR_SUFFIX;
        }
        return String.format(THIRD_PARTY_SENSOR_URL_TEMPLATE, appId);
    }

    public static JSONObject getEnforcerConfig() {
        JSONObject fromClasspath = readEnforcerConfigFromClasspath();
        if (fromClasspath != null) {
            return fromClasspath;
        }
        String filePath = legacyFilesystemConfigPath();
        JSONObject fromFile = readJsonFile(filePath);
        if (fromFile != null) {
            return fromFile;
        }
        throw new RuntimeException("Failed to load enforcer config '" + ENFORCER_CONFIG + JSON_SUFFIX
                + "' from classpath or from path: " + filePath);
    }

    /**
     * Best-effort path used by legacy IDE/exploded layouts (not reliable inside a {@code .war}).
     */
    public static String getEnforcerConfigPath() {
        return legacyFilesystemConfigPath();
    }

    private static JSONObject readEnforcerConfigFromClasspath() {
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(ENFORCER_CONFIG + JSON_SUFFIX)) {
            if (is == null) {
                return null;
            }
            try (Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                return new JSONObject(new Gson().fromJson(reader, HashMap.class));
            }
        } catch (Exception e) {
            return null;
        }
    }

    private static String legacyFilesystemConfigPath() {
        try {
            return Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource(""))
                    .getPath().replace(COMPILED_FILES_BASIC_PATH, "")
                    + RESOURCES_RELATIVE_PATH + ENFORCER_CONFIG + JSON_SUFFIX;
        } catch (Exception e) {
            return "(unknown)";
        }
    }

    private static JSONObject readJsonFile(String path) {
        try {
            Reader reader = Files.newBufferedReader(Paths.get(path));
            return new JSONObject(new Gson().fromJson(reader, HashMap.class));
        } catch (Exception e) {
            return null;
        }
    }
}
