package com.web;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static com.web.Constants.*;

public class Utils {

    public static Set<String> jsonArrayToSet(JSONArray jsonArray){
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
        final String filePath = getEnforcerConfigPath();
        try {
            return (String) Objects.requireNonNull(readJsonFile(filePath))
                    .get(PX_APP_ID_FIELD);
        } catch (JsonParseException jpe) {
            throw new RuntimeException("Failed to extract App ID from file :: " + filePath + ".\n Exception :: " + jpe);
        }
    }

    private static String getSensorSrc(String appId, Config config) {
        if (config.getPxConfiguration().isFirstPartyEnabled()) {
            return appId.replace(PX_PREFIX,"") + FIRST_PARTY_SENSOR_SUFFIX;
        }
        return String.format(THIRD_PARTY_SENSOR_URL_TEMPLATE,appId);
    }

    public static JSONObject getEnforcerConfig() {
        final String filePath = getEnforcerConfigPath();
        try {
            return readJsonFile(filePath);
        } catch (JsonParseException jpe) {
            throw new RuntimeException("Failed to extract config from file :: " + filePath + ".\n Exception :: " + jpe);
        }
    }

    public static String getEnforcerConfigPath() {
        return Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource(""))
                .getPath().replace(COMPILED_FILES_BASIC_PATH,"")
                + RESOURCES_RELATIVE_PATH + ENFORCER_CONFIG + JSON_SUFFIX;
    }

    private static JSONObject readJsonFile(String path){
        try {
            Reader reader = Files.newBufferedReader(Paths.get(path));
            return new JSONObject(new Gson().fromJson(reader, HashMap.class));
        } catch (Exception e) {
            return null;
        }
    }
}
