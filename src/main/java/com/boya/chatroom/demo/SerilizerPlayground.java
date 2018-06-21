package com.boya.chatroom.demo;

import com.boya.chatroom.Util.JacksonSerializer;
import com.boya.chatroom.domain.Message;
import com.boya.chatroom.domain.Response;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.util.Scanner;

public class SerilizerPlayground {

    public static void main(String[] args) {
        try {

            while (true){
                JacksonSerializer<Message> jacksonSerializer = new JacksonSerializer<>();
                JacksonSerializer<Response> jacksonSerializerRes = new JacksonSerializer<>();

                Scanner scanner = new Scanner(System.in);
                String input = scanner.nextLine();

                if (input.equals("EXIT")){
                    System.exit(0);
                }

                Message message3 = Message.msgNowChat("boya", "siyuan", input);
                String messageStr = jacksonSerializer.objToStr(message3);


                System.out.println(messageStr);

                Message messageBack = jacksonSerializer.strToObj(messageStr, Message.class);
                System.out.println(messageBack);
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
