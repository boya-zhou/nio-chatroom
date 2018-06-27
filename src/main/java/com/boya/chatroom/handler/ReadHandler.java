package com.boya.chatroom.handler;

import com.boya.chatroom.domain.Response;
import com.boya.chatroom.enums.ByteBufferSetting;
import com.boya.chatroom.util.JacksonSerializer;
import com.boya.chatroom.domain.Message;
import com.boya.chatroom.domain.MessageType;
import com.boya.chatroom.exception.EmptyMessageException;
import com.boya.chatroom.handler.msghandler.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class ReadHandler implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(ReadHandler.class);
    private final BlockingQueue<SocketChannel> toBeClosedChannel;
    private ByteBuffer byteBuffer = ByteBuffer.allocate(ByteBufferSetting.DEFAULT.getSize());

    private SelectionKey selectionKey;
    private ConcurrentHashMap<String, SocketChannel> channelMap;

    public ReadHandler(SelectionKey selectionKey,
                       ConcurrentHashMap<String, SocketChannel> channelMap, BlockingQueue<SocketChannel> toBeClosedChannel) {
        this.selectionKey = selectionKey;
        this.channelMap = channelMap;
        this.toBeClosedChannel = toBeClosedChannel;
    }

    @Override
    public void run() {

        SocketChannel socketChannel = (SocketChannel) this.selectionKey.channel();
        byteBuffer.clear();

        try {
            if (socketChannel.read(byteBuffer) == -1) {
                logger.info("Connection closed from: " + socketChannel.getRemoteAddress());
                toBeClosedChannel.offer(socketChannel);
                socketChannel.close();

            } else {
                while (socketChannel.read(byteBuffer) != 0) {
                    continue;
                }
                byteBuffer.flip();

                if (byteBuffer.limit() == 0) {
                    throw new EmptyMessageException();
                }

                Message message = getMsg(byteBuffer);
                logger.info("Server received: " + message.toString());
                msgDispatcher(socketChannel, message);
            }
        } catch (EmptyMessageException | JsonProcessingException e) {
            logger.error("Message received can not be deserialize");
            new MsgWrongTypeHandler(channelMap, null).handle(socketChannel);
        } catch (IOException e) {
            logger.error("Unable to handle message in " + ReadHandler.class.getName(), e);
        } finally {
            if (selectionKey.isValid() && socketChannel.isConnected()) {
                selectionKey.interestOps(selectionKey.interestOps() | SelectionKey.OP_READ);
                selectionKey.selector().wakeup();
            }
        }
    }

    /**
     * Select message handler and handle message
     *
     * @param socketChannel
     * @param message
     */
    private void msgDispatcher(SocketChannel socketChannel, Message message) {

        MessageType msgType = message.getMessageHeader().getType();
        MsgHandler msgHandler;

        switch (msgType) {
            case LOGIN:
                msgHandler = new LoginHandler(channelMap, message);
                break;
            case LOGOUT:
                msgHandler = new LogoutHandler(channelMap, message);
                break;
            case CHAT:
                msgHandler = new ChatHandler(channelMap, message);
                break;
            case FRIENDS_LIST:
                msgHandler = new FriendsListHandler(channelMap, message);
                break;
            default:
                msgHandler = new MsgWrongTypeHandler(channelMap, message);
        }

        msgHandler.handle(socketChannel);
    }

    private Message getMsg(ByteBuffer byteBuffer) throws JsonMappingException, JsonProcessingException, IOException {

        String messageStr = new String(byteBuffer.array(), Charset.forName("UTF-8"));

        JacksonSerializer<Message> jacksonSerializer = new JacksonSerializer<>();

        Message message = jacksonSerializer.strToObj(messageStr, Message.class);

        return message;
    }

}
