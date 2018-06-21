package com.boya.chatroom.domain;

public enum ResponseCode {
    SUCCESS(200, "success"),
    BAD_REQUEST(400, "bad request"),
    INTERNAL_SERVER_ERROR(500, "internal server error");

    private int code;
    private String desc;

    ResponseCode(int code, String desc) {
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
        return "ResponseCode{" +
                "code=" + code +
                ", desc='" + desc + '\'' +
                '}';
    }
}
