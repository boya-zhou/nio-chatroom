package com.boya.chatroom.handler.msghandler;

import com.boya.chatroom.domain.Message;
import com.boya.chatroom.domain.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentHashMap;

public class ChatHandler extends MsgBasicHandler {

    private static Logger logger = LoggerFactory.getLogger(ChatHandler.class);

    public ChatHandler(ConcurrentHashMap<String, SocketChannel> channelMap, Message message) {
        super(channelMap, message);
    }

    /**
     * Send the message to receiver, if write to receiver socketchannel, send success to sender,
     * else, send server internal error to sender
     * @param socketChannel
     */
    @Override
    public void handle(SocketChannel socketChannel) {

        String sender = message.getMessageHeader().getSender();
        String receiver = message.getMessageHeader().getReceiver();

        Response responseForReceiver;
        Response responseForSender;

        try {
            if (channelMap.keySet().contains(receiver)) {
                try {
                    responseForReceiver = Response.successChat(sender, receiver, message.getBody());
                    writeBuffer(channelMap.get(receiver), responseForReceiver);
                } catch (IOException e) {
                    logger.error("Sending chat message to receiver: " + receiver + " failed", e);
                    // tell sender receiver didn't received the message
                    responseForSender = Response.internalError();
                    writeBuffer(channelMap.get(sender), responseForSender);
                }
                responseForSender = Response.success();
            } else {
                responseForSender = Response.successFriendNotOnline();
            }

            writeBuffer(channelMap.get(sender), responseForSender);

        } catch (IOException e) {
            logger.error("Sending chat message to sender: " + sender + " failed", e);
        }
    }
}
