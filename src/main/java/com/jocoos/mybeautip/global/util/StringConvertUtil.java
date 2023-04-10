package com.jocoos.mybeautip.global.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

@Slf4j
public class StringConvertUtil {

    public static String convertToJson(Object object) {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException ex) {
            log.error("failed convert object to json string.", ex);
        }
        return null;
    }

    public static Map<String, Object> convertJsonToMapObject(String jsonString) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
            });
        } catch (JsonProcessingException ex) {
            log.error("failed convert json string to Map<String, Object>. string >>" + jsonString, ex);
        }

        return null;
    }

    public static Map<String, String> convertJsonToMap(String jsonString) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(jsonString, new TypeReference<Map<String, String>>() {
            });
        } catch (JsonProcessingException ex) {
            log.error("failed convert json string to Map<String, String>. string >>" + jsonString, ex);
        }

        return null;
    }

    public static String convertMapToJson(Map<String, String> map) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(map);
        } catch (JsonProcessingException ex) {
            log.error("failed convert map to json string. map >>" + map, ex);
        }

        return null;
    }

    public static String convertDoubleMapToJson(Map<String, Map<String, String>> map) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(map);
        } catch (JsonProcessingException ex) {
            log.error("failed convert map to json string. map >>" + map, ex);
        }

        return null;
    }

    public static String getPath(String url) {
        try {
            URI uri = new URI(url);
            return uri.getPath();
        } catch (URISyntaxException e) {
            log.error("{}", e);
        }
        return null;
    }

}