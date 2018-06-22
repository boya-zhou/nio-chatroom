package com.boya.chatroom.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketOption;
import java.net.StandardSocketOptions;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

public class AcceptHandler implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(AcceptHandler.class);

    private ExecutorService executorService;
    public Selector selector;
    public ConcurrentHashMap<String, SocketChannel> channelMap;

    public AcceptHandler(ExecutorService executorService,
                         Selector selector,
                         ConcurrentHashMap<String, SocketChannel> channelMap) {
        this.executorService = executorService;
        this.selector = selector;
        this.channelMap = channelMap;
    }

    /**
     * No writable is needed: stackoverflow EJP
     *
     * @throws IOException
     */
    private void listen() throws IOException {

        while (!Thread.currentThread().isInterrupted()) {

            this.selector.select();
            Set<SelectionKey> keys = this.selector.selectedKeys();
            Iterator<SelectionKey> iterator = keys.iterator();

            while (iterator.hasNext()) {

                SelectionKey key = iterator.next();
                iterator.remove();

                if (key.isAcceptable()) {
                    handleAcceptableKeys(key);

                } else if (key.isReadable()) {

                    key.interestOps(key.interestOps() & (~SelectionKey.OP_READ));
                    ReadHandler readHandlerTask = new ReadHandler(key, channelMap);
                    executorService.execute(readHandlerTask);

                } else {
                    logger.info("key is not acceptable nor readable", key);
                }

            }
        }
    }

    public void shutdown(){
        Thread.currentThread().interrupt();
    }

    private void handleAcceptableKeys(SelectionKey key) {

        try {
            ServerSocketChannel serverSocketChannel1 = (ServerSocketChannel) key.channel();
            SocketChannel socketChannel = serverSocketChannel1.accept();
            socketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
            logger.info("Server connected to client: " + socketChannel.getRemoteAddress());
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);
        } catch (ClosedChannelException e) {
            logger.error(e.getMessage(), e);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

    }

    @Override
    public void run() {
        try {
            listen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
