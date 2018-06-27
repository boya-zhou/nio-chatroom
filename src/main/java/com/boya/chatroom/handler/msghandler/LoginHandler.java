package com.boya.chatroom.handler.msghandler;

import com.boya.chatroom.domain.Message;
import com.boya.chatroom.domain.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentHashMap;

public class LoginHandler extends MsgBasicHandler {
    private static Logger logger = LoggerFactory.getLogger(LoginHandler.class);

    public LoginHandler(ConcurrentHashMap<String, SocketChannel> channelMap, Message message) {
        super(channelMap, message);
    }

    /**
     * Without any exception, login should do two things:
     * 1. broadcast the login info of new user to all online user
     * 2. tell new login user the list of online users
     *
     * @param socketChannel
     */
    @Override
    public void handle(SocketChannel socketChannel) {

        String sender = message.getMessageHeader().getSender();
        Response responseToSender;
        boolean success = false;

        synchronized (channelMap) {
            if (channelMap.containsKey(sender)) {
                responseToSender = Response.badRequest();
            } else {
                Response responseToAll = Response.successFriendLogin(sender);
                broadcast(responseToAll);
                channelMap.put(sender, socketChannel);
                responseToSender = Response.success();
                success = true;
            }
        }

        try {
            writeBuffer(socketChannel, responseToSender);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        if (success) {

            Response responseFriendList = null;

            try {
                responseFriendList = Response.successFriendList(sender, channelMap.keySet());
            } catch (JsonProcessingException e) {
                logger.error("The channel can not be serialized, fix this right now");
            }

            try {
                writeBuffer(socketChannel, responseFriendList);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }

    }
}
