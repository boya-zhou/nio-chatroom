package com.boya.chatroom.handler.msghandler;

import com.boya.chatroom.domain.Message;
import com.boya.chatroom.domain.Response;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentHashMap;

public class LoginHandler extends MsgBasicHandler {

    public LoginHandler(ConcurrentHashMap<String, SocketChannel> channelMap, Message message) {
        super(channelMap, message);
    }

    @Override
    public void handle(SocketChannel socketChannel) throws IOException {

        // login should do two things
        // 1. broadcast the login of new user to all online user
        // 2. tell the new user Online users

        // TODO: test if this is approprite
        // synchronized channelmap, send broadcast message to all user
        // prevent other user login while
        String sender = message.getMessageHeader().getSender();
        Response response = Response.successFriendLogin(sender);
        broadcast(response);
        // Add to channel map to show the login status
        channelMap.putIfAbsent(sender, socketChannel);
        Response responseFriendList = Response.successFriendList(sender, channelMap.keySet());
        writeBuffer(socketChannel, responseFriendList);
    }
}
