package com.boya.chatroom.server;

import com.boya.chatroom.demo.BasicClient;
import com.boya.chatroom.handler.AcceptHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.*;

public class Server implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(BasicClient.class);
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 8080;

    private ExecutorService executorService =
            new ThreadPoolExecutor(1,
                    3,
                    3,
                    TimeUnit.SECONDS,
                    new ArrayBlockingQueue<Runnable>(10));
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

        }catch (IOException e){
            e.printStackTrace();
        }

    }

    public void shutdown() throws IOException {
        selector.close();
        executorService.shutdown();
        acceptHandler.shutdown();
        serverSocketChannel.close();
        System.exit(0);
    }

    @Override
    public void run() {
            init();
            new Thread(acceptHandler).start();
    }
}
