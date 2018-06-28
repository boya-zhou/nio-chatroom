package com.boya.chatroom.util;

import com.boya.chatroom.domain.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;

import static org.junit.Assert.*;

public class ResponseWarpperTest {

    @Test
    public void addLength() {

        Message message = Message.msgNowChat("boya", "siyuan", "hi siyuan");

        JacksonSerializer<Message> jacksonSerializer = new JacksonSerializer<>();


        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        byteBuffer = ResponseWarpper.addLength(byteBuffer, message);

        byteBuffer.flip();

        byte[] dst = new byte[1024];

        int length = byteBuffer.getInt();

        byteBuffer.get(dst, 0, length);


        try {
            Message message1 = jacksonSerializer.bytesToObj(dst, Message.class);
            System.out.println(message1.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}