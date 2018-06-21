package com.boya.chatroom.exception;

public class MessageFormatError extends RuntimeException {

    public MessageFormatError() {
        super("The Message Format is not correct");
    }
}
