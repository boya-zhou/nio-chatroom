package com.boya.chatroom.demo;

import java.nio.Buffer;
import java.nio.IntBuffer;

public class BufferDemo {

    public static void main(String[] args) {
        IntBuffer intBuffer = IntBuffer.allocate(2);

        intBuffer.put(1234567890);
        intBuffer.put(234566666);
        intBuffer.put(222222222);

        intBuffer.flip();

        int data = 0;

        while(data != -1){
            data = intBuffer.get();
            System.out.println(data);
        }
    }
}
