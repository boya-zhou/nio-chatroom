package com.boya.chatroom.playground;

import com.boya.chatroom.Util.JacksonSerializer;
import com.boya.chatroom.client.NewClient;
import com.boya.chatroom.domain.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class BasicClient implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(BasicClient.class);

    public void simpleSocketConnect() {

        try {

            InetAddress inetAddress = InetAddress.getByName(NewClient.DEFAULT_HOST);
            InetSocketAddress inetSocketAddress = new InetSocketAddress(inetAddress, NewClient.DEFAULT_PORT);

            logger.info("connecting to server");

            SocketChannel socketChannel = SocketChannel.open(inetSocketAddress);
            //socketChannel.configureBlocking(false);

            // sendMessageNaive(socketChannel);

            Scanner scanner = new Scanner(System.in).useDelimiter("\n");
            String input = null;

            while(true){

                input = scanner.nextLine();
                if (input.equals("EXIT")){
                    break;
                }

                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

                System.out.println("I insert: " + input);
                Message message = Message.msgNowLogin("boya");
                byteBuffer.clear();

                JacksonSerializer<Message> jacksonSerializer = new JacksonSerializer<>();
                byteBuffer.put(jacksonSerializer.objToStr(message).getBytes());

                byteBuffer.flip();

                while (byteBuffer.hasRemaining()) {
                    socketChannel.write(byteBuffer);
                }
////
////                byteBuffer.clear();
////
////                // Thread.sleep(1000);
////
////                int data = socketChannel.read(byteBuffer);
////
////                if (data == 0) {
////                    logger.info("no message received");
////                } else if (data == -1){
////                    logger.info("the server is down");
////                    socketChannel.close();
////                } else{
////                    logger.info("message received from server" + new String(byteBuffer.array(), "UTF-8").trim());
////                }
            }

            // socketChannel.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.exit(0);
    }

    private void sendMessageNaive(SocketChannel socketChannel) throws IOException {

        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        for (int i = 0; i < 5; i++) {

            byteBuffer.clear();
            byteBuffer.put(("Hello socket channel server from boya: " + i) .getBytes());

            byteBuffer.flip();

            while (byteBuffer.hasRemaining()) {
                socketChannel.write(byteBuffer);
            }

            byteBuffer.clear();

            // Thread.sleep(1000);

            int data = socketChannel.read(byteBuffer);

            if (data == 0) {
                logger.info("no message received");
            } else if (data == -1){
                logger.info("the server is down");
                socketChannel.close();
            } else{
                logger.info("message received from server" + new String(byteBuffer.array(), "UTF-8").trim());
            }
        }
    }

    @Override
    public void run() {
        simpleSocketConnect();
    }
}
