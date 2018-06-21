package com.boya.chatroom.domain;

public enum MessageType {

    LOGIN(0, "login"),
    LOGOUT(1, "logout"),
    ADD_FRIEND(2, "add friend"),
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

    public void setCode(int code) {
        this.code = code;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "MessageType{" +
                "code=" + code +
                ", desc='" + desc + '\'' +
                '}';
    }
}
