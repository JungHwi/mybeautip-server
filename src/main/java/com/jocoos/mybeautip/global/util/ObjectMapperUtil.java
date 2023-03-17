package com.jocoos.mybeautip.global.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;

import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class ObjectMapperUtil {

    @SuppressWarnings("unchecked")
    public static Map<String, Object> converToMap(Object convertObject, ObjectMapper objectMapper) {
        return objectMapper.convertValue(convertObject, Map.class);
    }
}
