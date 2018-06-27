package com.boya.chatroom.domain;

public enum MessageType {

    LOGIN(0, "login"),
    LOGOUT(1, "logout"),
    FRIENDS_LIST(2, "friends list"),
    CHAT(3, "chat");

    private int code;
    private String desc;

    MessageType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    @Override
    public String toString() {
        return "MessageType{" +
                "code=" + code +
                ", desc='" + desc + '\'' +
                '}';
    }
}
