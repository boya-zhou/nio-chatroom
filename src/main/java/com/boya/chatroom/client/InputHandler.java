package com.boya.chatroom.client;

import com.boya.chatroom.Util.JacksonSerializer;
import com.boya.chatroom.domain.Message;
import com.boya.chatroom.domain.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class InputHandler implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(InputHandler.class);
    private static String EXIT_OPTION = "EXIT";
    private JacksonSerializer<Message> msgSerializer = new JacksonSerializer<>();

    private ByteBuffer sendBuf;
    private String nickName;
    private Scanner scanner = new Scanner(System.in).useDelimiter("\n");
    private SocketChannel socketChannel;


    public InputHandler(String nickName,
                        SocketChannel socketChannel,
                        ByteBuffer sendBuf) {
        this.nickName = nickName;
        this.socketChannel = socketChannel;
        this.sendBuf = sendBuf;
    }

    @Override
    public void run() {

        logger.info("begin to sendMsg to server");
        try {
            sendMsg();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;


    }

    private void sendMsg() throws IOException {

        String input = null;
        while (true) {

            System.out.println("EXIT for logout, C for continue");
            input = scanner.nextLine();

            if (input.equals(EXIT_OPTION)){
                Message message = Message.msgNowLogout(this.nickName);
                new SendingHandler(socketChannel, sendBuf, msgSerializer).sendMessage(message);
            }

            Message message = msgCreate();
            if (message == null) continue;
            new SendingHandler(socketChannel, sendBuf, msgSerializer).sendMessage(message);

        }
    }

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
