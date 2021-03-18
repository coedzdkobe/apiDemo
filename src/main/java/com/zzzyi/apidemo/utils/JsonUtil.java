package com.zzzyi.apidemo.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * json转换类
 *
 * @author Administrator
 */
public class JsonUtil {

    private static ObjectMapper mapper = new ObjectMapper();

    public static String objectTojson(Object object) {

        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("json转换失败:" + e.getMessage());
        }

    }

    public static <T> T jsonToObject(String json, Class<T> orderClass) {

        try {
            return mapper.readValue(json, orderClass);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("json转换失败:" + e.getMessage());
        }
    }
}
