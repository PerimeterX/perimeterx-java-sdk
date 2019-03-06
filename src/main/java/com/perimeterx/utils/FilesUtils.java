package com.perimeterx.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.perimeterx.models.configuration.PXConfiguration;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Scanner;

import static com.perimeterx.utils.JsonUtils.pxConfigurationReader;

public class FilesUtils {


    public static Map<String, String> readFileConfigAsMap(String filepath) throws IOException {
        String data = readFile(filepath);
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        return gson.fromJson(data, type);
    }

    public static PXConfiguration readFileConfigAsPXConfig(String filepath) throws IOException {
        String data = readFile(filepath);
        return pxConfigurationReader.readValue(data);

    }

    public static String readFile(String filepath) throws FileNotFoundException {
        Scanner in = new Scanner(new FileReader(filepath));
        StringBuilder sb = new StringBuilder();
        while (in.hasNext()) {
            sb.append(in.next());
        }
        in.close();
        return sb.toString();
    }
}
