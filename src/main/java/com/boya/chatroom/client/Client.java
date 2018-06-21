package com.boya.chatroom.client;

import com.boya.chatroom.Util.JacksonSerializer;
import com.boya.chatroom.domain.Message;
import com.boya.chatroom.domain.MessageType;
import com.boya.chatroom.domain.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.*;

public class Client implements Runnable {

    public static final String DEFAULT_HOST = "localhost";
    public static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_BUFFER_SIZE = 1024;
    private static final int DEFAULT_RESULT_WAIT_TIME = 30;

    private String nickName;
    private Selector selector;

    // list of friends nickname
    // TODO: this function did not work yet
    private List<String> friends;

    private Map<String, List<String>> chatlog;

    private SocketChannel socketChannel = null;
    private ByteBuffer recvBuf = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);
    private ByteBuffer sendBuf = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);
    private JacksonSerializer<Message> msgSerializer = new JacksonSerializer<>();

    private static Logger logger = LoggerFactory.getLogger(Client.class);

    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    public Client() {
        Scanner scanner = new Scanner(System.in).useDelimiter("\n");
        System.out.println("Please input your nickname, do not contain any white space separators");
        this.nickName = scanner.nextLine();
    }

    /**
     * Init the connection with Server
     */
    private void init() throws IOException {

        InetAddress inetAddress = InetAddress.getByName(Client.DEFAULT_HOST);
        InetSocketAddress inetSocketAddress = new InetSocketAddress(inetAddress, Client.DEFAULT_PORT);
        socketChannel = SocketChannel.open(inetSocketAddress);
        logger.info("Connect to: " + socketChannel.getRemoteAddress());
        socketChannel.configureBlocking(false);
        selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_READ);

        // login
        login();
    }

    private void login() throws IOException {

        Message message = Message.msgNowLogin(this.nickName);
        sendMessage(message);

        Response response = getResponse();
        logger.info(response.toString());
        logger.info(new String(response.getBody(), Charset.forName("UTF-8")));
    }

    private Response getResponse() {
        // start listening
        ListeningHandler clientListeningHandler = new ListeningHandler(selector, recvBuf);
        Future<Response> responseFuture = executorService.submit(clientListeningHandler);

        // TODO: how to handle these exception in global way
        Response response = null;
        try {
            response = responseFuture.get(DEFAULT_RESULT_WAIT_TIME, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    public void run() {
        try {
            init();
            communicate();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void communicate() throws IOException {

        logger.info("begin to communicate to server");

        Scanner scanner = new Scanner(System.in).useDelimiter("\n");
        String input = null;

        while (true) {

            System.out.println("EXIT for quit, C for continue: ");

            input = scanner.nextLine();

            if (input.equals("EXIT")) {
                Message logOutReq = Message.msgNowLogout(this.nickName);
                sendMessage(logOutReq);

                Response response = getResponse();
                logger.info(response.toString());
                break;
            }

            Message message = msgCreate();

            if (message == null) {
                continue;
            }

            sendMessage(message);
            Response response = getResponse();

            logger.info(response.toString());
            String bodyStr = response.getBody() == null ? "null" : new String(response.getBody(), Charset.forName("UTF-8"));
            logger.info(bodyStr);
        }
    }



    private void sendMessage(Message message) throws IOException {

        sendBuf.clear();
        sendBuf.put(msgSerializer.objToStr(message).getBytes());
        sendBuf.flip();

        while (sendBuf.hasRemaining()) {
            socketChannel.write(sendBuf);
        }
    }

    /**
     * Create a valid message from scanner
     *
     * @return
     */
    private Message msgCreate() {

        System.out.println("Please input your request");
        System.out.println("Select 1 of following options: ");
        System.out.println("2: Add new friend");
        System.out.println("3: Chat");

        Scanner scanner = new Scanner(System.in).useDelimiter("\n");
        int request = Integer.valueOf(scanner.nextLine());

        do {
            if (request == MessageType.ADD_FRIEND.getCode()) {
                System.out.println("Please insert your new friend name: ");
                String friendName = scanner.nextLine();
                System.out.println("Please insert what you want to say to new friend: ");
                String body = scanner.nextLine();
                return Message.msgNowAddFriend(this.nickName, friendName, body);

            } else if (request == MessageType.CHAT.getCode()) {

                System.out.println("Please insert your friend name: ");
                String friendName = scanner.nextLine();
                System.out.println("Please insert what you want to say to friend: ");
                String body = scanner.nextLine();
                return Message.msgNowChat(this.nickName, friendName, body);

            } else {

                String option = null;

                do {
                    System.out.println("The option is not valid");
                    System.out.println("Do you want to quit? Y for yes and N for no");
                    option = scanner.nextLine();

                    if (option.equals("Y")) {
                        break;
                    } else if (option.equals("N")) {
                        continue;
                    }
                } while ((!option.equals("Y")) || (!option.equals("N")));

                if (option.equals("Y")) {
                    break;
                } else {
                    continue;
                }

            }
        } while ((request != MessageType.ADD_FRIEND.getCode()) || (request != MessageType.CHAT.getCode()));

        return null;
    }

}
