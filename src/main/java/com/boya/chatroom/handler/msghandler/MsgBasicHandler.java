package com.boya.chatroom.handler.msghandler;

import com.boya.chatroom.enums.ByteBufferSetting;
import com.boya.chatroom.util.JacksonSerializer;
import com.boya.chatroom.domain.Message;
import com.boya.chatroom.domain.Response;
import com.boya.chatroom.util.ResponseWarpper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class MsgBasicHandler implements MsgHandler {

    public ConcurrentHashMap<String, SocketChannel> channelMap;
    public Message message;

    private static Logger logger = LoggerFactory.getLogger(MsgBasicHandler.class);

    public MsgBasicHandler(ConcurrentHashMap<String, SocketChannel> channelMap, Message message) {
        this.channelMap = channelMap;
        this.message = message;
    }

    /**
     * Broadcast the login information to all user
     * @param response login information
     */
    public void broadcast(Response response) {
        synchronized (channelMap) {
            for (Map.Entry<String, SocketChannel> ele : channelMap.entrySet()) {
                SocketChannel socketChannel = ele.getValue();
                if (socketChannel != null && socketChannel.isConnected()) {
                    try {
                        writeBuffer(socketChannel, response);
                    } catch (JsonProcessingException e) {
                        String responseStr = response == null ? "null" : response.toString();
                        logger.error("The response can not be serilized. " +  responseStr);
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                    }
                }else {
                    channelMap.remove(ele.getKey());
                }
            }
        }
    }

    /**
     * Write the response back to sender channel, because multiple place will use this method,
     * so throw the exceptions out
     *
     * @param <T>
     * @param currentChannel
     * @param response
     * @throws JsonProcessingException
     * @throws IOException
     */
    public static <T> void writeBuffer(SocketChannel currentChannel, T response) throws JsonProcessingException, IOException {

        ByteBuffer sendBuf = ByteBuffer.allocate(ByteBufferSetting.DEFAULT.getSize());
        sendBuf.clear();
        sendBuf = ResponseWarpper.addLength(sendBuf, response);
        sendBuf.flip();

        while (sendBuf.hasRemaining()) {
            currentChannel.write(sendBuf);
        }
    }

    @Override
    public void handle(SocketChannel socketChannel) {

    }
}
