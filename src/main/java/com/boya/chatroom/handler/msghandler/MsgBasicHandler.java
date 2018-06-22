package com.boya.chatroom.handler.msghandler;

import com.boya.chatroom.Util.JacksonSerializer;
import com.boya.chatroom.domain.Message;
import com.boya.chatroom.domain.Response;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentHashMap;

public abstract class MsgBasicHandler implements MsgHandler{

    public ConcurrentHashMap<String, SocketChannel> channelMap;
    public Message message;

    public MsgBasicHandler(ConcurrentHashMap<String, SocketChannel> channelMap, Message message) {
        this.channelMap = channelMap;
        this.message = message;
    }

    public void broadcast(Response response) throws IOException {
        synchronized (channelMap) {
            for (SocketChannel onlineChannel : channelMap.values()) {
                if (onlineChannel.isConnected()) {
                    writeBuffer(onlineChannel, response);
                }
            }
        }
    }

    public <T> void writeBuffer(SocketChannel onlineChannel, T response) throws IOException {
        JacksonSerializer<T> jacksonSerializer = new JacksonSerializer<>();

        ByteBuffer sendBuf = ByteBuffer.allocate(1024);
        sendBuf.clear();
        sendBuf.put(jacksonSerializer.objToStr(response).getBytes());
        sendBuf.flip();

        while(sendBuf.hasRemaining()){
            onlineChannel.write(sendBuf);
        }
    }

    @Override
    public void handle(SocketChannel socketChannel) throws IOException {

    }
}
