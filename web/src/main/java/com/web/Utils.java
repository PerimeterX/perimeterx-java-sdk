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

import static com.web.Constants.ENFORCER_CONFIG;
import static com.web.Constants.JSON_SUFFIX;

public class Utils {
    private final static String RESOURCES_RELATIVE_PATH = "src/main/resources/";

    public static Set<String> jsonArrayToSet(JSONArray jsonArray){
        Set<String> stringsSet = new HashSet<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            stringsSet.add(jsonArray.getString(i));
        }
        return stringsSet;
    }

    public static void setDefaultPageAttributes(HttpServletRequest request) {
        final String appId = getAppId();
        request.setAttribute("app_id", appId);
        request.setAttribute("sensor_src_url", "/" + getSensorSrc(appId));
    }

    private static String getAppId() {
        final String filePath = getEnforcerConfigPath();
        try {
            return (String) Objects.requireNonNull(readJsonFile(filePath))
                    .get("px_app_id");
        } catch (JsonParseException jpe) {
            throw new RuntimeException("Failed to extract App ID from file :: " + filePath + ".\n Exception :: " + jpe);
        }
    }

    private static String getSensorSrc(String appId) {
        if (isFirstParty()) {
            return appId.replace("PX","") + "/init.js";
        }
        return String.format( "//client.px-cloud.net/%s/main.min.js",appId);
    }

    private static boolean isFirstParty() {
        return (boolean) getEnforcerConfig().get("px_first_party_enabled");
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
                .getPath().replace("/target/classes","")
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
