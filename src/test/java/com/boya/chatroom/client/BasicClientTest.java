package com.boya.chatroom.client;

import com.boya.chatroom.playground.BasicClient;
import org.junit.Test;

public class BasicClientTest {

    @Test
    public void simpleSocketConnect() {

//        BasicClient basicClient = new BasicClient();
//        basicClient.run();

        for (int i = 0; i < 1; i++) {
            new Thread(new BasicClient()).start();
        }

        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 1; i++) {
            new Thread(new BasicClient()).start();
        }

        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}