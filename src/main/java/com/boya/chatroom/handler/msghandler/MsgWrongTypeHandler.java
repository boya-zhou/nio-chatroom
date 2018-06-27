package com.boya.chatroom.handler.msghandler;

import com.boya.chatroom.domain.Message;
import com.boya.chatroom.domain.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentHashMap;

public class MsgWrongTypeHandler extends MsgBasicHandler {

    private static Logger logger = LoggerFactory.getLogger(MsgBasicHandler.class);

    public MsgWrongTypeHandler(ConcurrentHashMap<String, SocketChannel> channelMap, Message message) {
        super(channelMap, message);
    }

    /**
     * Tell the sender he/she send a invalid request
     * @param socketChannel
     */
    @Override
    public void handle(SocketChannel socketChannel) {

        Response response = Response.badRequest();
        try {
            writeBuffer(socketChannel, response);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
