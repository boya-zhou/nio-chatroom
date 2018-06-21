package com.boya.chatroom.playground;

import com.boya.chatroom.client.NewClient;

public class ClientStarter {

    public static void main(String[] args) {

        NewClient client = new NewClient();
        client.run();

//        for (int i = 0; i < 1; i++) {
//            // new Thread(new BasicClient()).start();
//            new Thread(new NewClient()).start();
//        }
//
//        try {
//            Thread.currentThread().join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }
}
