package com.boya.chatroom.handler.msghandler;

import com.boya.chatroom.domain.Message;
import com.boya.chatroom.domain.Response;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentHashMap;

public class LogoutHandler extends MsgBasicHandler {

    public LogoutHandler(ConcurrentHashMap<String, SocketChannel> channelMap, Message message) {
        super(channelMap, message);
    }

    /**
     *
     * @param socketChannel
     */
    @Override
    public void handle(SocketChannel socketChannel){

        String sender = message.getMessageHeader().getSender();
        Response response = Response.successFriendLogout(sender);
        broadcast(response);
        channelMap.remove(sender, socketChannel);
        try {
            socketChannel.close();
        } catch (IOException e) {
            // Since this is a single server app, IOException here doesn't matter
        }
    }
}
