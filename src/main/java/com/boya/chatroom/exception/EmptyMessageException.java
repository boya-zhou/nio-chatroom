package com.boya.chatroom.exception;

public class EmptyMessageException extends RuntimeException {

    public EmptyMessageException() {
        super("Should not send empty message");
    }

}
