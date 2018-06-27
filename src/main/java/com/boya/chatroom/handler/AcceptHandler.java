package com.boya.chatroom.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.StandardSocketOptions;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

public class AcceptHandler implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(AcceptHandler.class);

    private ExecutorService executorService;
    private Selector selector;
    private ConcurrentHashMap<String, SocketChannel> channelMap;
    private BlockingQueue<SocketChannel> toBeClosedChannel;

    public AcceptHandler(ExecutorService executorService,
                         Selector selector,
                         ConcurrentHashMap<String, SocketChannel> channelMap,
                         BlockingQueue<SocketChannel> blockingQueue) {
        this.executorService = executorService;
        this.selector = selector;
        this.channelMap = channelMap;
        this.toBeClosedChannel = blockingQueue;
    }

    /**
     * Listen for ready key
     */
    private void listen() {

        while (true) {
            try {
                this.selector.select();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
            Set<SelectionKey> keys = this.selector.selectedKeys();
            Iterator<SelectionKey> iterator = keys.iterator();

            while (iterator.hasNext()) {

                SelectionKey key = iterator.next();
                iterator.remove();

                if (key.isAcceptable()) {
                    handleAcceptableKeys(key);
                } else if (key.isReadable()) {
                    key.interestOps(key.interestOps() & (~SelectionKey.OP_READ));
                    ReadHandler readHandlerTask = new ReadHandler(key, channelMap, toBeClosedChannel);
                    executorService.execute(readHandlerTask);
                } else {
                    logger.info("key is not acceptable nor readable", key);
                }
            }
        }
    }


    /**
     * Handle acceptable key
     *
     * @param key
     */
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
        listen();
    }
}
