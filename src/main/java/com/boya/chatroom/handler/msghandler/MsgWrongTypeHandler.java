package com.boya.chatroom.handler.msghandler;

import com.boya.chatroom.domain.Message;
import com.boya.chatroom.domain.Response;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentHashMap;

public class MsgWrongTypeHandler extends MsgBasicHandler {

    public MsgWrongTypeHandler(ConcurrentHashMap<String, SocketChannel> channelMap, Message message) {
        super(channelMap, message);
    }

    @Override
    public void handle(SocketChannel socketChannel) throws IOException {

        Response response = Response.badRequest(message.getMessageHeader().getSender());
        writeBuffer(socketChannel, response);
    }
}
