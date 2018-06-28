package com.boya.chatroom.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

public class ResponseWarpper {

    public static Logger logger = LoggerFactory.getLogger(ResponseWarpper.class);

    public static <T> ByteBuffer addLength(ByteBuffer byteBuffer, T pojo){

        JacksonSerializer<T> jacksonSerializer = new JacksonSerializer<>();
        byte[] bytes;
        try {
            bytes = jacksonSerializer.objTobytes(pojo);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage(), e);
            return byteBuffer;
        }
        byteBuffer.putInt(bytes.length).put(bytes);

        return byteBuffer;
    }
}
