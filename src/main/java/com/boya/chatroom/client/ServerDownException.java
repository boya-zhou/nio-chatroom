package com.boya.chatroom.client;

public class ServerDownException extends RuntimeException {

    public ServerDownException() {
        super("The server is down");
    }
}
