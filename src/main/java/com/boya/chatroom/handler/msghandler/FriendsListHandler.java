package com.boya.chatroom.handler.msghandler;

import com.boya.chatroom.domain.Message;
import com.boya.chatroom.domain.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentHashMap;

public class FriendsListHandler extends MsgBasicHandler {

    private static Logger logger = LoggerFactory.getLogger(FriendsListHandler.class);


    public FriendsListHandler(ConcurrentHashMap<String, SocketChannel> channelMap, Message message) {
        super(channelMap, message);
    }

    @Override
    public void handle(SocketChannel socketChannel) {
        try {
            Response responseFriendList = Response.successFriendList(message.getMessageHeader().getSender(), channelMap.keySet());
            writeBuffer(socketChannel, responseFriendList);
        } catch (JsonProcessingException e) {
            logger.error("The channel list can not be serialized, fix this right now");
        } catch (IOException e){
            logger.error(e.getMessage(), e);
        }
    }
}
