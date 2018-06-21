package com.boya.chatroom.domain;

public enum ResponseType {
    FRIEND_LOGIN(0, "friend enter"),
    FRIEND_LOGOUT(1, "friend leave"),
    FRIEND_LIST(2, "list of friend online"),
    FRIEND_MESSAGE(3, "friend send message"),
    FRIEND_REQUEST(4, "a new friend send request"),
    FRIEND_NOT_ONLINE(5, "friend not online!");

    private int code;
    private String desc;

    ResponseType(int code, String desc) {
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
        return "ResponseType{" +
                "code=" + code +
                ", desc='" + desc + '\'' +
                '}';
    }
}
