package com.boya.chatroom.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketOption;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

public class AcceptHandler implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(AcceptHandler.class);

    private ExecutorService executorService;
    public Selector selector;
    public ConcurrentHashMap<String, SocketChannel> channelMap;

    public AcceptHandler(ExecutorService executorService, Selector selector, ConcurrentHashMap<String, SocketChannel> channelMap) {
        this.executorService = executorService;
        this.selector = selector;
        this.channelMap = channelMap;
    }

    /**
     * No writable is needed
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

                    ReadHandler readHandlerTask = new ReadHandler(key, selector, channelMap);
                    // new Thread(readHandlerTask).start();
                    executorService.execute(readHandlerTask);
                } else {
                    System.out.println("when key is not accept, read, write");
                }

            }
        }
    }

    public void shutdown() {
        Thread.currentThread().interrupt();
    }

    private void handleAcceptableKeys(SelectionKey key) throws IOException {

        ServerSocketChannel serverSocketChannel1 = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = serverSocketChannel1.accept();
        socketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
        logger.info("Server connected to client: " + socketChannel.getRemoteAddress());

        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);

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
