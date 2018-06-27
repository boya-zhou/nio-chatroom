package com.boya.chatroom.client;

import com.boya.chatroom.domain.Response;
import com.boya.chatroom.domain.ResponseCode;

public class SystemInfoUtil {

    public static boolean nameCheckPass(Response response){

        if ((response == null) || (response.getResponseHeader() == null) || (response.getResponseHeader().getReponseCode() == null)){
            return false;
        }

        if (response.getResponseHeader().getReponseCode().getCode() == ResponseCode.SUCCESS.getCode()){
            return true;
        }

        return false;

    }
}
