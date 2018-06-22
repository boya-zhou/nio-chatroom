package com.boya.chatroom.handler;

import com.boya.chatroom.util.JacksonSerializer;
import com.boya.chatroom.domain.Message;
import com.boya.chatroom.domain.MessageType;
import com.boya.chatroom.exception.EmptyMessageException;
import com.boya.chatroom.handler.msghandler.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.concurrent.ConcurrentHashMap;

public class ReadHandler implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(ReadHandler.class);

    private SelectionKey selectionKey;
    private ConcurrentHashMap<String, SocketChannel> channelMap;
    private ByteBuffer byteBuffer = ByteBuffer.allocate(1024);


    public ReadHandler(SelectionKey selectionKey,  ConcurrentHashMap<String, SocketChannel> channelMap) {
        this.selectionKey = selectionKey;
        this.channelMap = channelMap;
    }

    @Override
    public void run() {

        SocketChannel socketChannel = (SocketChannel) this.selectionKey.channel();
        byteBuffer.clear();

        try {

            if (socketChannel.read(byteBuffer) == -1) {
                logger.info("Connection closed from: " + socketChannel.getRemoteAddress());
                // remove from channel map as well, this is just for in case since normal remove will be done in logout handler
                for (String key : channelMap.keySet()) {
                    if (channelMap.get(key).equals(socketChannel)){
                        channelMap.remove(socketChannel);
                    }
                }
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
                int msgType = message.getMessageHeader().getType().getCode();
                msgDispatcher(socketChannel, message, msgType);
            }

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } catch (EmptyMessageException e) {
            logger.error(e.getLocalizedMessage(), e);
        } finally {
            // register can not save selectionKey, rather it will return a new key
            // socketChannel.register(selector, SelectionKey.OP_READ);

            // begin next round listening
            if (selectionKey.isValid() && socketChannel.isConnected()){
                selectionKey.interestOps(selectionKey.interestOps() | SelectionKey.OP_READ);
                selectionKey.selector().wakeup();
            }
        }
    }

    // TODO, use spring to handle these four
    private void msgDispatcher(SocketChannel socketChannel, Message message, int msgType) throws IOException {

        MsgHandler msgHandler = null;

        if (msgType == MessageType.LOGIN.getCode()) {

            msgHandler = new LoginHandler(channelMap, message);
            msgHandler.handle(socketChannel);

        } else if (msgType == MessageType.LOGOUT.getCode()) {

            msgHandler = new LogoutHandler(channelMap, message);
            msgHandler.handle(socketChannel);

        } else if (msgType == MessageType.ADD_FRIEND.getCode()) {
            // TODO this function not work right now
            /**
             String receiver = message.getMessageHeader().getReceiver();
             Response response = Response.successFriendRequest(sender, receiver);

             if (channelMap.keySet().contains(receiver)) {
             SocketChannel receiverSocketChannel = channelMap.get(receiver);
             writeBuffer(receiverSocketChannel, response);
             }**/

        } else if (msgType == MessageType.CHAT.getCode()) {
            msgHandler = new ChatHandler(channelMap, message);
            msgHandler.handle(socketChannel);

        } else {
            logger.info("Strange, the message format is not correct", message);
            msgHandler = new MsgWrongTypeHandler(channelMap, message);
            msgHandler.handle(socketChannel);
        }
    }

    private Message getMsg(ByteBuffer byteBuffer) throws IOException {

        String messageStr = new String(byteBuffer.array(), Charset.forName("UTF-8"));

        JacksonSerializer<Message> jacksonSerializer = new JacksonSerializer<>();

        Message message = jacksonSerializer.strToObj(messageStr, Message.class);

        return message;
    }


}
