package com.boya.chatroom.playground;

import java.nio.ByteBuffer;

public class IntToByteArray {

    public static void main(String[] args) {


        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        byteBuffer.putInt(1004);


        ByteBuffer newByteBuffer = ByteBuffer.wrap(byteBuffer.array());
        System.out.println(newByteBuffer.getInt(1));


    }

    public static byte[] toBytes(int i){

        byte[] newBytes = new byte[4];

        newBytes[0] = (byte)(i >> 24);
        newBytes[1] = (byte)(i >> 16);
        newBytes[2] = (byte)(i >> 8);
        newBytes[3] = (byte)(i);

        return newBytes;
    }
}
