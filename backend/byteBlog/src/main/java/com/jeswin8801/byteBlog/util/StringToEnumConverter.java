package com.jeswin8801.byteBlog.util;

import com.jeswin8801.byteBlog.util.exceptions.ByteBlogException;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

public class StringToEnumConverter implements ConverterFactory<String, Enum> {

    @Override
    public <T extends Enum> Converter<String, T> getConverter(Class<T> targetType) {
        return source -> {
            try {
                return (T) Enum.valueOf(targetType, source.toUpperCase());
            } catch (Exception e) {
                throw new ByteBlogException(e.getMessage());
            }
        };
    }
}
