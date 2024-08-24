package com.jeswin8801.byteBlog.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.io.Serializable;
import java.util.Base64;

@UtilityClass
public class AppUtil {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Serializes an object
     */
    public <T> String serialize(T object) {
        try {
            return Base64.getUrlEncoder().encodeToString(
                    objectMapper.writeValueAsBytes(object)
            );
        } catch (JsonProcessingException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Deserializes an Object
     */
    public <T> T deserialize(String serializedObject,
                             Class<T> className) {
        try {
            return objectMapper.readValue(
                    Base64.getDecoder().decode(serializedObject),
                    className
            );
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Serializes an object to JSON string
     */
    public <T> String toJson(T object) {

        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Deserializes a JSON String
     */
    public static <T> T fromJson(String json,
                                 Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

}
