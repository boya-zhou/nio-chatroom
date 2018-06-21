package com.boya.chatroom.demo;

import com.boya.chatroom.client.Client;

public class ClientPlayGround {

    public static void main(String[] args) {

        Client client = new Client();
        client.run();
//        for (int i = 0; i < 1; i++) {
//            // new Thread(new BasicClient()).start();
//            new Thread(new Client()).start();
//        }
//
//        try {
//            Thread.currentThread().join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }
}
