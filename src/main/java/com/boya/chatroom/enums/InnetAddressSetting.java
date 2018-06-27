package com.boya.chatroom.enums;

public enum InnetAddressSetting {

    DEFAULT_ADDRESS("localhost", 8080);

    public String localhost;
    public int port;

    InnetAddressSetting(String localhost, int port) {
        this.localhost = localhost;
        this.port = port;
    }
}
