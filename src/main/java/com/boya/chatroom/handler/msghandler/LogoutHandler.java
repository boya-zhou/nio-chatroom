package com.boya.chatroom.handler.msghandler;

import com.boya.chatroom.domain.Message;
import com.boya.chatroom.domain.Response;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentHashMap;

public class LogoutHandler extends MsgBasicHandler {

    public LogoutHandler(ConcurrentHashMap<String, SocketChannel> channelMap) {
        super(channelMap);
    }

    @Override
    public void handle(Message message, SocketChannel socketChannel) throws IOException {

        String sender = message.getMessageHeader().getSender();

        Response response = Response.successFriendLogout(sender);
        broadcast(sender, response);
        channelMap.remove(sender, socketChannel);
    }
}
