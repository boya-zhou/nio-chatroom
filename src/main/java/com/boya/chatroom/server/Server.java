package com.boya.chatroom.server;

import com.boya.chatroom.handler.AcceptHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.*;
import java.util.concurrent.*;

public class Server implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(Server.class);
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 8080;

    private ExecutorService executorService =
            new ThreadPoolExecutor(3,
                    50,
                    3,
                    TimeUnit.SECONDS,
                    new ArrayBlockingQueue<Runnable>(50));
    private ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> friendMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, SocketChannel> channelMap = new ConcurrentHashMap<>();

    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    private SocketAddress socketAddress;

    private AcceptHandler acceptHandler;
    /**
     * Init the ServerSocketChannel
     *
     * @throws IOException
     */
    private void init() {

        try {
            serverSocketChannel = ServerSocketChannel.open();
            socketAddress = new InetSocketAddress(DEFAULT_HOST, DEFAULT_PORT);
            selector = Selector.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            serverSocketChannel.bind(socketAddress);
            logger.info("Server open socket: " + serverSocketChannel.getLocalAddress());
            acceptHandler = new AcceptHandler(executorService, selector, channelMap);

        } catch (ClosedChannelException e) {
            logger.error("The server socket channel is not open yet");
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void shutdown() throws IOException {
        selector.close();
        executorService.shutdown();
        acceptHandler.shutdown();
        serverSocketChannel.close();
        logger.info("Server shutdown");
        System.exit(0);
    }

    @Override
    public void run() {
            init();
            new Thread(acceptHandler).start();
    }
}
