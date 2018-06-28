package com.boya.chatroom.enums;

public enum ByteBufferSetting {

    DEFAULT(1024),
    LARGE(32*1024);

    private int size;

    ByteBufferSetting(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }
}
