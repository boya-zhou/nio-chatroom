package com.boya.chatroom.client;

import com.boya.chatroom.Util.JacksonSerializer;
import com.boya.chatroom.domain.Message;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class SendingHandler {

    private SocketChannel socketChannel;
    private ByteBuffer sendBuf;
    private JacksonSerializer<Message> msgSerializer;

    public SendingHandler(SocketChannel socketChannel, ByteBuffer sendBuf, JacksonSerializer<Message> msgSerializer) {
        this.socketChannel = socketChannel;
        this.sendBuf = sendBuf;
        this.msgSerializer = msgSerializer;
    }

    public void sendMessage(Message message) throws IOException {

        sendBuf.clear();
        sendBuf.put(msgSerializer.objToStr(message).getBytes());
        sendBuf.flip();

        while (sendBuf.hasRemaining()) {
            socketChannel.write(sendBuf);
        }
    }

}
