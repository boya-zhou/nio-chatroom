package com.boya.chatroom.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class MessageHeader {

    private String sender;
    private String receiver;
    private MessageType Type;
    private LocalDateTime localDateTime;

    @JsonCreator
    public MessageHeader(@JsonProperty("sender") String sender,@JsonProperty("receiver") String receiver,
                         @JsonProperty("type") MessageType type, @JsonProperty("localDateTime") LocalDateTime localDateTime) {
        this.sender = sender;
        this.receiver = receiver;
        Type = type;
        this.localDateTime = localDateTime;
    }

    public MessageHeader(String sender, String receiver, MessageType type) {
        this.sender = sender;
        this.receiver = receiver;
        Type = type;
        this.localDateTime = LocalDateTime.now();
    }

    public MessageHeader(String sender, MessageType type) {
        this.sender = sender;
        Type = type;
        this.localDateTime = LocalDateTime.now();
    }

    public static MessageHeader msgNowLogin(String sender){
        return new MessageHeader(sender, MessageType.LOGIN);
    }

    public static MessageHeader msgNowLogout(String sender){
        return new MessageHeader(sender, MessageType.LOGOUT);
    }

    public static MessageHeader msgNowAddFriend(String sender, String receiver){
        return new MessageHeader(sender, receiver, MessageType.ADD_FRIEND);
    }

    public static MessageHeader msgNowChat(String sender, String receiver){
        return new MessageHeader(sender, receiver, MessageType.CHAT);
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public MessageType getType() {
        return Type;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    @Override
    public String toString() {
        return "MessageHeader{" +
                "sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", Type=" + Type +
                ", localDateTime=" + localDateTime +
                '}';
    }
}
