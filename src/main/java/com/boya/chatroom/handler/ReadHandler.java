package com.boya.chatroom.handler;

import com.boya.chatroom.handler.msghandler.ChatHandler;
import com.boya.chatroom.handler.msghandler.LoginHandler;
import com.boya.chatroom.handler.msghandler.LogoutHandler;
import com.boya.chatroom.handler.msghandler.MsgHandler;
import com.boya.chatroom.Util.JacksonSerializer;
import com.boya.chatroom.domain.Message;
import com.boya.chatroom.domain.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.concurrent.ConcurrentHashMap;

public class ReadHandler implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(ReadHandler.class);

    private SelectionKey selectionKey;
    private Selector selector;
    private ConcurrentHashMap<String, SocketChannel> channelMap;

    public ReadHandler(SelectionKey selectionKey, Selector selector, ConcurrentHashMap<String, SocketChannel> channelMap) {
        this.selectionKey = selectionKey;
        this.selector = selector;
        this.channelMap = channelMap;
    }

    @Override
    public void run() {

        SocketChannel socketChannel = (SocketChannel) this.selectionKey.channel();

        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.clear();

        try {
            int size = 0;

            if (socketChannel.read(byteBuffer) == -1) {
                logger.info("Connection closed from: " + socketChannel.getRemoteAddress());
                socketChannel.close();
            } else {
                while (socketChannel.read(byteBuffer) != 0) {
                    continue;
                }
                byteBuffer.flip();
                Message message = getMsg(byteBuffer);

                logger.info("Server received: " + message.toString());

                int msgType = message.getMessageHeader().getType().getCode();

                msgDispatcher(socketChannel, message, msgType);
                // socketChannel.register(selector, SelectionKey.OP_READ);
                // TODO: check what these two line do
                selectionKey.interestOps(selectionKey.interestOps() | SelectionKey.OP_READ);
                selectionKey.selector().wakeup();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void msgDispatcher(SocketChannel socketChannel, Message message, int msgType) throws IOException {

        MsgHandler msgHandler = null;

        if (msgType == MessageType.LOGIN.getCode()) {

            msgHandler = new LoginHandler(channelMap);
            msgHandler.handle(message, socketChannel);

        } else if (msgType == MessageType.LOGOUT.getCode()) {

            msgHandler = new LogoutHandler(channelMap);
            msgHandler.handle(message, socketChannel);

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
            msgHandler = new ChatHandler(channelMap);
            msgHandler.handle(message, socketChannel);

        } else {
            logger.info("Strange, the message format is not correct");
        }
    }

    private Message getMsg(ByteBuffer byteBuffer) throws IOException {

        String messageStr = new String(byteBuffer.array(), Charset.forName("UTF-8"));

        JacksonSerializer<Message> jacksonSerializer = new JacksonSerializer<>();

        Message message = jacksonSerializer.strToObj(messageStr, Message.class);

        return message;
    }


}
