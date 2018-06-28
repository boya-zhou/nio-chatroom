package com.boya.chatroom.playground;

import com.boya.chatroom.domain.Message;
import com.boya.chatroom.util.JacksonSerializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class JacksonPlayGround {

    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();
        Message message1 = Message.msgNowChat("boya", "siyuan", "hi siyuan");
        Message message2 = Message.msgNowLogout("boya");

        JacksonSerializer<Message> jacksonSerializer = new JacksonSerializer<>();

        try {
            byte[] bytes1 = objectMapper.writeValueAsBytes(message1);
            byte[] bytes2 = objectMapper.writeValueAsBytes(message2);

            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            byteBuffer.putInt(bytes1.length).put(bytes1).putInt(bytes2.length).put(bytes2);

            byteBuffer.flip();

            int length;

            while(byteBuffer.position() < byteBuffer.limit()){
                length = byteBuffer.getInt();

                System.out.println(byteBuffer.position());
                System.out.println(byteBuffer.limit());

                byte[] resBytebuffer = new byte[length];
                byteBuffer.get(resBytebuffer,0, length);
                System.out.println(new String(resBytebuffer, Charset.forName("UTF-8")));

                try {
                    jacksonSerializer.bytesToObj(resBytebuffer, Message.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
