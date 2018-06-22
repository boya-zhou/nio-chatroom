package com.boya.chatroom.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JacksonSerializer<T> {

    private ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    public String objToStr(T obj) throws JsonProcessingException {
        return this.objectMapper.writeValueAsString(obj);
    }

    public T strToObj(String obj, Class<T> clazz) throws IOException {
        return this.objectMapper.readValue(obj, clazz);
    }

}
