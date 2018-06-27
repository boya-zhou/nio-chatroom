package com.boya.chatroom.domain;

import com.boya.chatroom.util.JacksonSerializer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Arrays;
import java.util.Set;

public class Response {

    private String sender;
    private String receiver;
    private ResponseHeader responseHeader;
    private byte[] body;

    @JsonCreator
    public Response(@JsonProperty("sender") String sender,
                    @JsonProperty("receiver") String receiver,
                    @JsonProperty("responseHeader") ResponseHeader responseHeader,
                    @JsonProperty("body") byte[] body) {
        this.sender = sender;
        this.receiver = receiver;
        this.responseHeader = responseHeader;
        this.body = body;
    }

    public Response(String sender, ResponseHeader responseHeader, byte[] body) {
        this.sender = sender;
        this.responseHeader = responseHeader;
        this.body = body;
    }

    public Response(String sender, ResponseHeader responseHeader) {
        this.sender = sender;
        this.responseHeader = responseHeader;
    }

    public Response(ResponseHeader responseHeader){
        this.responseHeader = responseHeader;
    }

    public static Response success(){
        return new Response(ResponseHeader.success());
    }

    public static Response successFriendNotOnline(){
        return new Response(ResponseHeader.successFriendNotOnline());
    }

    public static Response successFriendLogin(String sender){
        return new Response(sender, ResponseHeader.successFriendLogin());
    }

    public static Response successFriendLogout(String sender){
        return new Response(sender, ResponseHeader.successFriendLogout());
    }

    public static Response successFriendList(String sender, Set<String> nameList) throws JsonProcessingException {
        JacksonSerializer<Set<String>> jacksonSerializer = new JacksonSerializer<>();
        return new Response(sender, ResponseHeader.successFriendList(), jacksonSerializer.objToStr(nameList).getBytes());
    }

    public static Response successChat(String sender, String receiver, byte[] body){
        return new Response(sender, receiver, ResponseHeader.successChat(), body);
    }

    public static Response badRequest(){
        return new Response(ResponseHeader.badRequest());
    }

    public static Response internalError(){
        return new Response(ResponseHeader.serverError());
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public ResponseHeader getResponseHeader() {
        return responseHeader;
    }

    public byte[] getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "Response{" +
                "sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", responseHeader=" + responseHeader +
                ", body=" + Arrays.toString(body) +
                '}';

    }


}
