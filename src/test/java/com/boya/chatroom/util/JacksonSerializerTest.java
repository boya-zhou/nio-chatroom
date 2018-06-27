package com.boya.chatroom.util;

import com.boya.chatroom.domain.Message;
import com.boya.chatroom.domain.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;

import java.io.IOException;
import java.util.Scanner;

public class JacksonSerializerTest {

    Message message = Message.msgNowChat("trillie", "siyuan", "today I want to eat yougurt");
    Response response = Response.successChat("trillie", "siyuan", "I am the most cute lab in the world".getBytes());

    Response response2 = Response.badRequest();

    Message message2 = Message.msgNowChat("boya", "trillie", "hi there");

    @Test
    public void objToStr() {

        JacksonSerializer<Message> jacksonSerializer = new JacksonSerializer<>();

        try {

            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();
            Message message3 = Message.msgNowChat("boya", "siyuan", input);
            String messageStr = jacksonSerializer.objToStr(message3);
            System.out.println(messageStr);

            Message messageBack = jacksonSerializer.strToObj(messageStr, Message.class);
            System.out.println(messageBack);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void objToStrResponse() {

        JacksonSerializer<Response> jacksonSerializer = new JacksonSerializer<>();

        try {
            serial(jacksonSerializer, response);
            serial(jacksonSerializer, response2);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void serial(JacksonSerializer<Response> jacksonSerializer, Response response) throws IOException {
        String responseStr = jacksonSerializer.objToStr(response);
        System.out.println(responseStr);

        Response messageBack = jacksonSerializer.strToObj(responseStr, Response.class);
        System.out.println(messageBack);
    }


}