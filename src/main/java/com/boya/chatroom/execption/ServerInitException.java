package com.boya.chatroom.execption;

import java.io.IOException;

public class ServerInitException extends IOException {
    public ServerInitException(String message) {
        super(message);
    }
}
