package com.boya.chatroom.playground;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.*;

public class BasicServer implements Runnable {

    public static final String LOCAL_HOST = "localhost";
    public static final int DEFAULT_PORT = 8080;

    private static Logger logger = LoggerFactory.getLogger(BasicClient.class);

    private static final Map<SocketChannel, Queue<ByteBuffer>> pendingData = new HashMap<>();

    public void simpleServer() {

        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            SocketAddress socketAddress = new InetSocketAddress(LOCAL_HOST, DEFAULT_PORT);
            serverSocketChannel.bind(socketAddress);
            serverSocketChannel.configureBlocking(false);

            logger.info("server open socket 8080");

            Selector selector = Selector.open();

            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {

                selector.select();

                Set<SelectionKey> selectionKeys = selector.selectedKeys();

                Iterator<SelectionKey> iterator = selectionKeys.iterator();

                while (iterator.hasNext()) {

                    SelectionKey selectionKey = iterator.next();
                    iterator.remove();

                    if (selectionKey.isAcceptable()) {
                        ServerSocketChannel serverSocketChannel1 = (ServerSocketChannel) selectionKey.channel();
                        SocketChannel socketChannel = serverSocketChannel1.accept();

                        logger.info("connected to: " + socketChannel.getLocalAddress());
                        // TODO research on configuringBlock
                        socketChannel.configureBlocking(false);

                        socketChannel.register(selector, SelectionKey.OP_READ);

                    } else if (selectionKey.isReadable()) {

                        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                        socketChannel.configureBlocking(false);

                        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

                        byteBuffer.clear();

                        int data = socketChannel.read(byteBuffer);

                        if (data == -1) {
                            logger.info("no message in client socket");
                            socketChannel.close();
                        } else {
                            byteBuffer.flip();
                            logger.info("socket server receive message " + new String(byteBuffer.array(), Charset.forName("UTF-8")));

                            //
                            byteBuffer.clear();

                            byteBuffer.put("Thank you for your message".getBytes());

                            byteBuffer.flip();

                            while (byteBuffer.hasRemaining()) {
                                socketChannel.write(byteBuffer);
                            }

                            socketChannel.register(selector, SelectionKey.OP_READ);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        simpleServer();
    }
}
