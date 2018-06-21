package com.boya.chatroom.server;

import com.boya.chatroom.demo.BasicServer;
import org.junit.Test;

public class BasicServerTest {

    @Test
    public void simpleServer() throws InterruptedException {

//        BasicServer basicServer = new BasicServer();
//        basicServer.simpleServer();

        new Thread(new Server()).start();

        Thread.currentThread().join();
    }
}