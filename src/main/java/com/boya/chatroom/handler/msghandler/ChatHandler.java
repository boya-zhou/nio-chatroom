package com.boya.chatroom.handler.msghandler;

import com.boya.chatroom.domain.Message;
import com.boya.chatroom.domain.Response;
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


    @Override
    public void handle(SocketChannel socketChannel) throws IOException {
        String sender = message.getMessageHeader().getSender();
        String receiver = message.getMessageHeader().getReceiver();

        logger.info(channelMap.toString());


        if (channelMap.keySet().contains(receiver)) {
            Response responseForReceiver = Response.successChat(sender, receiver, message.getBody());
            writeBuffer(channelMap.get(receiver), responseForReceiver);

            Response responseForSender = Response.success();
            writeBuffer(channelMap.get(sender), responseForSender);
        } else {
            Response responseForSender = Response.successFriendNotOnline();
            writeBuffer(channelMap.get(sender), responseForSender);
        }

    }
}
