package com.boya.chatroom.client;

import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChatLogUtil {

    // simple date format is not thread safe
    public static String formatter(String chat, String sender, String receiver){
        String chatStr = StringUtils.isEmpty(chat) ? "null" : chat;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return sender + " says to " + receiver + " : " + chatStr +  " | At time: " + dateTimeFormatter.format(LocalDateTime.now());
    }

    public static void main(String[] args) {
        System.out.println(formatter("hi", "boya", "siyuan"));
    }

    public static void printChatLog(String[] chatLog){
        for (String chat: chatLog){
            System.out.println(chat);
        }
    }
}
