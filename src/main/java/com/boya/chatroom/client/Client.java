package com.boya.chatroom.client;

import com.boya.chatroom.domain.Response;
import com.boya.chatroom.domain.ResponseCode;
import com.boya.chatroom.enums.ByteBufferSetting;
import com.boya.chatroom.enums.InnetAddressSetting;
import com.boya.chatroom.util.JacksonSerializer;
import com.boya.chatroom.domain.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Scanner;
import java.util.concurrent.*;

public class Client implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(Client.class);

    private String nickName;
    private Selector selector;
    private SocketChannel socketChannel = null;
    private LinkedBlockingQueue<String> chatLog = new LinkedBlockingQueue<>(300);
    private LinkedBlockingQueue<Response> systemInfo = new LinkedBlockingQueue<>(300);
    private LinkedBlockingQueue<String> friendList = new LinkedBlockingQueue<>(300);

    // Each client assume 5 thread is enough
    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    private InputHandler inputHandler;
    private ListeningHandler listeningHandler;
    private ByteBuffer recvBuf = ByteBuffer.allocate(ByteBufferSetting.LARGE.getSize());
    private ByteBuffer sendBuf = ByteBuffer.allocate(ByteBufferSetting.DEFAULT.getSize());
    private JacksonSerializer<Message> msgSerializer = new JacksonSerializer<>();

    private CountDownLatch countDownLatch = new CountDownLatch(1);


    private void init() {

        try {
            InetAddress inetAddress = InetAddress.getByName(InnetAddressSetting.DEFAULT_ADDRESS.localhost);
            InetSocketAddress inetSocketAddress = new InetSocketAddress(inetAddress, InnetAddressSetting.DEFAULT_ADDRESS.port);
            socketChannel = SocketChannel.open(inetSocketAddress);
            logger.info("Connect to: " + socketChannel.getRemoteAddress());
            socketChannel.configureBlocking(false);
            selector = Selector.open();
            socketChannel.register(selector, SelectionKey.OP_READ);

        } catch (UnknownHostException e) {
            logger.error(e.getMessage(), e);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void sendCheckName() {
        Scanner scanner = new Scanner(System.in).useDelimiter("\n");

        while (true) {
            System.out.println("Please input your nickname, do not contain any white space separators");
            this.nickName = scanner.nextLine();

            Message message = Message.msgNowLogin(this.nickName);

            try {
                SendingUtil.sendMessage(message, socketChannel, sendBuf, msgSerializer);
            } catch (ClosedChannelException e) {
                logger.error(e.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("Waiting for system checking...");

            while (systemInfo.size() == 0) {
                continue;
            }

            if (systemInfo.poll().getResponseHeader().getReponseCode().getCode() == ResponseCode.SUCCESS.getCode()) {
                System.out.println("Login success");
                break;
            } else {
                System.out.println("your name is repeat, please choose another name");
                continue;
            }
        }
    }

    private void sendingMsg() {
        this.inputHandler = new InputHandler(nickName, socketChannel, sendBuf, chatLog);
        executorService.execute(inputHandler);
    }

    private void listeningMsg() {
        this.listeningHandler = new ListeningHandler(selector, recvBuf, chatLog, systemInfo);
        executorService.execute(listeningHandler);
    }

    public void shutdown() {
        executorService.shutdown();
        try {
            socketChannel.close();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void run() {
        init();
        // after listening msg begin to run, sendCheckName and sendingMsg can begin
        try {
            listeningMsg();
        } catch (ServerDownException e) {
            logger.info("The remote server is down: ");
            logger.info("closing right now...");
            shutdown();
        }

        sendCheckName();
        sendingMsg();
    }
}
