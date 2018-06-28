package com.boya.chatroom.util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JacksonSerializer<T> {

    private ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    public String objToStr(T obj) throws JsonProcessingException {
        return this.objectMapper.writeValueAsString(obj);
    }

    public byte[] objTobytes(T obj) throws JsonProcessingException{
        return this.objectMapper.writeValueAsBytes(obj);
    }

    public T strToObj(String obj, Class<T> clazz) throws JsonParseException, JsonMappingException, IOException {
        return this.objectMapper.readValue(obj, clazz);
    }

    public T bytesToObj(byte[] byteArray, Class<T> clazz) throws JsonParseException, JsonMappingException, IOException {
        return this.objectMapper.readValue(byteArray, clazz);
    }

}
