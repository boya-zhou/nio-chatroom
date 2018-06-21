package com.boya.chatroom.handler.msghandler;

import com.boya.chatroom.domain.Message;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public interface MsgHandler {
    void handle(Message message, SocketChannel socketChannel) throws IOException;
}
