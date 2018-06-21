package com.boya.chatroom.client;

import com.boya.chatroom.Util.JacksonSerializer;
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
import java.util.concurrent.Callable;

public class ListeningHandler implements Callable {

    private Selector selector;
    private ByteBuffer recvBuf;

    private static Logger logger = LoggerFactory.getLogger(ListeningHandler.class);
    private JacksonSerializer<Response> resSerializer = new JacksonSerializer<>();

    public ListeningHandler(Selector selector, ByteBuffer recvBuf) {
        this.selector = selector;
        this.recvBuf = recvBuf;
    }

    @Override
    public Response call() {

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
                            logger.info("The remote server is down: " + socketChannel.getRemoteAddress());
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

                        return response;
                    }

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.warn("The response is null, something must want wrong");
        return null;
    }

}
