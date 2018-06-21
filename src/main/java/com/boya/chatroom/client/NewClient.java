package com.boya.chatroom.client;

import com.boya.chatroom.Util.JacksonSerializer;
import com.boya.chatroom.domain.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NewClient implements Runnable {

    public static final String DEFAULT_HOST = "localhost";
    public static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_BUFFER_SIZE = 1024;
    private String nickName;
    private Selector selector;

    private SocketChannel socketChannel = null;

    private static Logger logger = LoggerFactory.getLogger(NewClient.class);

    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    private InputHandler inputHandler;
    private ReadHandler readHandler;

    private ByteBuffer recvBuf = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);
    private ByteBuffer sendBuf = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);

    private JacksonSerializer<Message> msgSerializer = new JacksonSerializer<>();


    public NewClient() {
        Scanner scanner = new Scanner(System.in).useDelimiter("\n");
        System.out.println("Please input your nickname, do not contain any white space separators");
        this.nickName = scanner.nextLine();
    }

    private void init() throws IOException {
        InetAddress inetAddress = InetAddress.getByName(NewClient.DEFAULT_HOST);
        InetSocketAddress inetSocketAddress = new InetSocketAddress(inetAddress, NewClient.DEFAULT_PORT);
        socketChannel = SocketChannel.open(inetSocketAddress);
        logger.info("Connect to: " + socketChannel.getRemoteAddress());
        socketChannel.configureBlocking(false);
        selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_READ);

        // login
        Message message = Message.msgNowLogin(this.nickName);
        new SendingHandler(socketChannel, sendBuf, msgSerializer).sendMessage(message);
    }

    private void sendingMsg() {
        this.inputHandler = new InputHandler(nickName, socketChannel, sendBuf);
        executorService.submit(inputHandler);
    }

    private void listeningMsg() {
        this.readHandler = new ReadHandler(selector, recvBuf);
        executorService.submit(readHandler);
    }

    @Override
    public void run() {
        try {
            init();
            sendingMsg();
            listeningMsg();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
