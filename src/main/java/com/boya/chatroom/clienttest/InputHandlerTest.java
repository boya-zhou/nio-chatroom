package com.boya.chatroom.clienttest;

import com.boya.chatroom.client.ChatLogUtil;
import com.boya.chatroom.client.SendingUtil;
import com.boya.chatroom.domain.Message;
import com.boya.chatroom.domain.MessageType;
import com.boya.chatroom.util.JacksonSerializer;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;

public class InputHandlerTest implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(InputHandlerTest.class);
    private static String EXIT_OPTION = "EXIT";
    private JacksonSerializer<Message> msgSerializer = new JacksonSerializer<>();
    private LinkedBlockingQueue<String> chatLog;

    private ByteBuffer sendBuf;
    private String nickName;
    private SocketChannel socketChannel;


    public InputHandlerTest(String nickName,
                            SocketChannel socketChannel,
                            ByteBuffer sendBuf,
                            LinkedBlockingQueue<String> chatLog) {
        this.nickName = nickName;
        this.socketChannel = socketChannel;
        this.sendBuf = sendBuf;
        this.chatLog = chatLog;
    }

    @Override
    public void run() {

        logger.info("begin to sendMsg to server");
        try {
            sendMsg();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void sendMsg() throws IOException {

        String input;
        while (true) {

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
            input = "C";

            if (input.equals(EXIT_OPTION)) {
                Message message = Message.msgNowLogout(this.nickName);
                SendingUtil.sendMessage(message, socketChannel, sendBuf, msgSerializer);
                System.out.println("Welcome back");
                System.exit(0);
                break;
            }

            Message message = msgCreate();
            if (message == null) continue;
            SendingUtil.sendMessage(message, socketChannel, sendBuf, msgSerializer);

        }
    }


    public Message msgCreate() {

        System.out.println("Please input your request");
        System.out.println("Select one number of following options: ");
        System.out.println("1: ChatLog");
        System.out.println("2: Check Who is online");
        System.out.println("3: Chat");

        int request = RandomUtils.nextInt(3) + 1;

        do {

            if (request == MessageType.FRIENDS_LIST.getCode()) {
                System.out.println("Asking for current friends online");
                return Message.msgNowFriendsList(this.nickName);

            } else if (request == MessageType.CHAT.getCode()) {

                // ToDo: change to valid name
                String friendName = String.valueOf(RandomUtils.nextInt(300));

                String body = RandomStringUtils.randomAlphabetic(8);
                this.chatLog.add(ChatLogUtil.formatter(body, this.nickName, friendName));
                return Message.msgNowChat(this.nickName, friendName, body);

            } else if(request == 1){
                System.out.println("ChatLog as follows");
                ChatLogUtil.printChatLog(this.chatLog.toArray(new String[this.chatLog.size()]));
                return null;

            }else {

                String option;

                do {
                    System.out.println("The option is not valid");
                    System.out.println("Do you want to quit? Y for yes and N for no");
                    option = "Y";

                    if (option.equals("Y")) {
                        try {
                            Message message = Message.msgNowLogout(this.nickName);
                            SendingUtil.sendMessage(message, socketChannel, sendBuf, msgSerializer);
                        } catch (IOException e) {
                            logger.error(e.getMessage(), e);
                        }
                        System.exit(0);

                    } else if (option.equals("N")) {
                        break;
                    }
                } while ((!option.equals("Y")) || (!option.equals("N")));

                continue;
            }
        } while ((request != MessageType.FRIENDS_LIST.getCode()) || (request != MessageType.CHAT.getCode()));

        return null;
    }
}
