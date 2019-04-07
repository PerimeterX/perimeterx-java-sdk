package com.perimeterx.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JSONUtilsTest {

    private JSONUtilsTest(){}

    public static boolean isJSONValid(String jsonInString ) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            mapper.readTree(jsonInString);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
