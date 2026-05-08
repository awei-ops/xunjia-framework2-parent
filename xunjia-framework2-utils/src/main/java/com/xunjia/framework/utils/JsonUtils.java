package com.xunjia.framework.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonUtils {

    private final static ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
    }

    public static String serialize(Object obj){
        String json = "";
        try {
            json = mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
        return json;
    }

    public static <T> T deserialize(String json, TypeReference<T> ref){
        T obj = null;
        try {
            obj = mapper.readValue(json, ref);
        } catch (Exception e){
            log.error(e.getMessage(), e);
        }
        return obj;
    }

}
