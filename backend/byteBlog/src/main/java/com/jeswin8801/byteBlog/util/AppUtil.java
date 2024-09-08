package com.jeswin8801.byteBlog.util;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.JavaSerializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@UtilityClass
public class AppUtil {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Kryo kryo = new Kryo();

    /**
     * Serializes an object to
     */
    public <T> String serialize(T object, Class<T> className) {
        kryo.register(className, new JavaSerializer());

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);

        kryo.writeObject(output, object);
        byte[] serializedObject = byteArrayOutputStream.toByteArray();

        output.close();
        try {
            byteArrayOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return Base64.getUrlEncoder().encodeToString(serializedObject);
    }

    /**
     * Deserializes an Object
     */
    public <T> T deserialize(String serializedObject,
                             Class<T> className) {
        kryo.register(className, new JavaSerializer());

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                Base64.getUrlDecoder().decode(
                        serializedObject.getBytes(StandardCharsets.UTF_8)
                )
        );
        Input input = new Input(byteArrayInputStream);

        T deserializedObject = kryo.readObject(input, className);
        input.close();
        try {
            byteArrayInputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return deserializedObject;
    }

    /**
     * Serializes an object to JSON string
     */
    public <T> String toJson(T object) {

        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Deserializes a JSON String
     */
    public static <T> T fromJson(String json,
                                 Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
