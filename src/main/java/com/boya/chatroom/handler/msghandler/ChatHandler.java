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

    public ChatHandler(ConcurrentHashMap<String, SocketChannel> channelMap) {
        super(channelMap);
    }

    @Override
    public void handle(Message message, SocketChannel socketChannel) throws IOException {
        String sender = message.getMessageHeader().getSender();
        String receiver = message.getMessageHeader().getReceiver();

        logger.info(channelMap.toString());

        Response responseForReceiver = Response.successChat(sender, receiver, message.getBody());
        if (channelMap.keySet().contains(receiver)) {
            writeBuffer(channelMap.get(receiver), responseForReceiver);

            Response responseForSender = Response.success();
            writeBuffer(channelMap.get(sender), responseForSender);
        } else {
            // logger.info("The friend is not online");
            Response responseForSender = Response.successFriendNotOnline();
            writeBuffer(channelMap.get(sender), responseForSender);
        }

    }
}
