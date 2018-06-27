package com.boya.chatroom.server;

import com.boya.chatroom.enums.InnetAddressSetting;
import com.boya.chatroom.handler.AcceptHandler;
import com.boya.chatroom.handler.ClosedChannelRemoveHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.*;
import java.util.concurrent.*;

public class Server implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(Server.class);

    private ExecutorService executorService =
            new ThreadPoolExecutor(3,
                    50,
                    3,
                    TimeUnit.SECONDS,
                    new ArrayBlockingQueue<Runnable>(50));

    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private ConcurrentHashMap<String, SocketChannel> channelMap = new ConcurrentHashMap<>();
    private BlockingQueue<SocketChannel> toBeClosedChannel = new LinkedBlockingQueue<>();

    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    private SocketAddress socketAddress;

    private AcceptHandler acceptHandler;

    private ClosedChannelRemoveHandler channelMapUpdateHandler;

    /**
     * Init the ServerSocketChannel
     *
     * @throws IOException
     */
    private void init() {

        try {
            serverSocketChannel = ServerSocketChannel.open();
            socketAddress = new InetSocketAddress(InnetAddressSetting.DEFAULT_ADDRESS.localhost,
                    InnetAddressSetting.DEFAULT_ADDRESS.port);
            selector = Selector.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            serverSocketChannel.bind(socketAddress);

            logger.info("Server open socket: " + serverSocketChannel.getLocalAddress());

            acceptHandler = new AcceptHandler(executorService, selector, channelMap, toBeClosedChannel);
            channelMapUpdateHandler = new ClosedChannelRemoveHandler(channelMap, toBeClosedChannel);

        } catch (ClosedChannelException e) {
            logger.error("The server socket channel is not open yet");
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void shutdown() throws IOException {
        executorService.shutdown();
        selector.close();
        serverSocketChannel.close();
        logger.info("Server shutdown");
        System.exit(0);
    }

    @Override
    public void run() {
        init();
        executorService.submit(acceptHandler);
        scheduledExecutorService.scheduleAtFixedRate(channelMapUpdateHandler, 0,60, TimeUnit.SECONDS);
    }
}
