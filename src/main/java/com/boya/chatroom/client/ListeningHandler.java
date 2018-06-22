package com.boya.chatroom.client;

import com.boya.chatroom.util.JacksonSerializer;
import com.boya.chatroom.domain.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

public class ListeningHandler implements Runnable {

    private Selector selector;
    private ByteBuffer recvBuf;

    private static Logger logger = LoggerFactory.getLogger(ListeningHandler.class);
    private JacksonSerializer<Response> resSerializer = new JacksonSerializer<>();

    public ListeningHandler(Selector selector, ByteBuffer recvBuf) {
        this.selector = selector;
        this.recvBuf = recvBuf;
    }

    @Override
    public void run() {
        listening();
    }

    public void shutdown(){
        Thread.currentThread().interrupt();
    }

    public void listening(){

        try {

            while (!Thread.currentThread().isInterrupted()) {

                selector.select();

                for (Iterator<SelectionKey> iterator = selector.selectedKeys().iterator(); iterator.hasNext(); ) {
                    SelectionKey selectionKey = iterator.next();
                    iterator.remove();

                    SocketChannel socketChannel = (SocketChannel) selectionKey.channel();

                    if (selectionKey.isReadable()) {

                        recvBuf.clear();
                        if (socketChannel.read(recvBuf) == -1) {
                            socketChannel.close();
                            throw new ServerDownException();
                        } else {
                            while (socketChannel.read(recvBuf) != 0) {
                                continue;
                            }
                        }
                        recvBuf.flip();
                        String res = new String(recvBuf.array(), Charset.forName("UTF-8"));
                        Response response = resSerializer.strToObj(res, Response.class);

                        logger.info(response.toString());
                        String bodyStr = response.getBody() == null ? "null" : new String(response.getBody(), Charset.forName("UTF-8"));
                        logger.info(bodyStr);
                    }

                }
            }

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        logger.warn("The response is null, something must went wrong, connect the server for help");
    }
}
