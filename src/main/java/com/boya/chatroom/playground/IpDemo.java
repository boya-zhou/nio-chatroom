package com.boya.chatroom.playground;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class IpDemo {

    public static void main(String[] args){

        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                try {
                    System.out.println(InetAddress.getLocalHost().getHostAddress());
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

}
