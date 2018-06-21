package com.boya.chatroom.playground;

import com.boya.chatroom.server.Server;

public class ServerStarter {

    public static void main(String[] args) {
        Server server = new Server();
        server.run();
    }
}
