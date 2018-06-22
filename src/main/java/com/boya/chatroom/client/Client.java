package com.boya.chatroom.client;

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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client implements Runnable {

    public static final String DEFAULT_HOST = "localhost";
    public static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_BUFFER_SIZE = 1024;
    private String nickName;
    private Selector selector;

    private SocketChannel socketChannel = null;

    private static Logger logger = LoggerFactory.getLogger(Client.class);

    // Each client assume 5 thread is enough
    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    private InputHandler inputHandler;
    private ListeningHandler listeningHandler;

    private ByteBuffer recvBuf = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);
    private ByteBuffer sendBuf = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);

    private JacksonSerializer<Message> msgSerializer = new JacksonSerializer<>();


    public Client() {
        Scanner scanner = new Scanner(System.in).useDelimiter("\n");
        System.out.println("Please input your nickname, do not contain any white space separators");
        this.nickName = scanner.nextLine();
    }

    private void init()  {

        try{
            InetAddress inetAddress = InetAddress.getByName(Client.DEFAULT_HOST);
            InetSocketAddress inetSocketAddress = new InetSocketAddress(inetAddress, Client.DEFAULT_PORT);
            socketChannel = SocketChannel.open(inetSocketAddress);
            logger.info("Connect to: " + socketChannel.getRemoteAddress());
            socketChannel.configureBlocking(false);
            selector = Selector.open();
            socketChannel.register(selector, SelectionKey.OP_READ);

            // login
            Message message = Message.msgNowLogin(this.nickName);
            SendingUtil.sendMessage(message, socketChannel, sendBuf, msgSerializer);
        }catch (UnknownHostException e) {
            logger.error(e.toString());
        } catch (ClosedChannelException e) {
            logger.error(e.toString());
        } catch (IOException e) {
            logger.error(e.toString());
        }

    }

    private void sendingMsg() {
        this.inputHandler = new InputHandler(nickName, socketChannel, sendBuf);
        executorService.submit(inputHandler);
    }

    private void listeningMsg() {
        this.listeningHandler = new ListeningHandler(selector, recvBuf);
        executorService.submit(listeningHandler);
    }

    public void shutdown(){
        listeningHandler.shutdown();
        inputHandler.shutdown();
        try {
            socketChannel.close();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void run() {
            init();
            sendingMsg();
            try{
                listeningMsg();
            }catch (ServerDownException e){
                logger.info("The remote server is down: ");
                logger.info("closing right now...");
                shutdown();
            }

    }
}
