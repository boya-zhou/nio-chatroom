package com.boya.chatroom.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ResponseHeader {

    private ResponseCode reponseCode;
    private ResponseType responseType;

    @JsonCreator
    public ResponseHeader(@JsonProperty("reponseCode") ResponseCode reponseCode,
                          @JsonProperty("responseType") ResponseType responseType) {
        this.reponseCode = reponseCode;
        this.responseType = responseType;
    }

    public ResponseHeader(ResponseCode reponseCode) {
        this.reponseCode = reponseCode;
    }

    public static ResponseHeader success(){
        return new ResponseHeader(ResponseCode.SUCCESS);
    }

    public static ResponseHeader successFriendNotOnline(){
        return new ResponseHeader(ResponseCode.SUCCESS, ResponseType.FRIEND_NOT_ONLINE);
    }

    public static ResponseHeader successFriendLogin(){
        return new ResponseHeader(ResponseCode.SUCCESS, ResponseType.FRIEND_LOGIN);
    }

    public static ResponseHeader successFriendLogout(){
        return new ResponseHeader(ResponseCode.SUCCESS, ResponseType.FRIEND_LOGOUT);
    }

    public static ResponseHeader successFriendList(){
        return new ResponseHeader(ResponseCode.SUCCESS, ResponseType.FRIEND_LIST);
    }

    public static ResponseHeader successChat(){
        return new ResponseHeader(ResponseCode.SUCCESS, ResponseType.FRIEND_MESSAGE);
    }

    public static ResponseHeader successAddFriend(){
        return new ResponseHeader(ResponseCode.SUCCESS, ResponseType.FRIEND_REQUEST);
    }

    public static ResponseHeader badRequest(){
        return new ResponseHeader(ResponseCode.BAD_REQUEST);
    }

    public static ResponseHeader serverError(){
        return new ResponseHeader(ResponseCode.INTERNAL_SERVER_ERROR);
    }


    public ResponseCode getReponseCode() {
        return reponseCode;
    }

    public ResponseType getResponseType() {
        return responseType;
    }

    public void setReponseCode(ResponseCode reponseCode) {
        this.reponseCode = reponseCode;
    }

    public void setResponseType(ResponseType responseType) {
        this.responseType = responseType;
    }

    @Override
    public String toString() {
        return "ResponseHeader{" +
                "reponseCode=" + reponseCode +
                ", responseType=" + responseType +
                '}';
    }
}
