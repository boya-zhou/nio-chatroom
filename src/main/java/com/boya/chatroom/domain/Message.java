package com.boya.chatroom.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

/**
 * The protocol in this chatroom every message follows
 */
public class Message {

    private MessageHeader messageHeader;
    private String body = "null";

    @JsonCreator
    public Message(@JsonProperty("messageHeader") MessageHeader messageHeader,
                   @JsonProperty("body") String body) {
        this.messageHeader = messageHeader;
        this.body = body;
    }

    public Message(MessageHeader messageHeader) {
        this.messageHeader = messageHeader;
    }

    public static Message msgNowLogin(String sender) {
        return new Message(MessageHeader.msgNowLogin(sender));
    }

    public static Message msgNowLogout(String sender) {
        return new Message(MessageHeader.msgNowLogout(sender));
    }


    public static Message msgNowFriendsList(String sender) {
        return new Message(MessageHeader.msgFriendsList(sender));
    }

    public static Message msgNowChat(String sender, String receiver, String content) {
        return new Message(MessageHeader.msgNowChat(sender, receiver), content);
    }

    public MessageHeader getMessageHeader() {
        return messageHeader;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {

        return "Message{" +
                "messageHeader=" + messageHeader +
                ", body=" + body +
                '}';
    }
}
