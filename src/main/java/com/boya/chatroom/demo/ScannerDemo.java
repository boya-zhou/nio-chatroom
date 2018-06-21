package com.boya.chatroom.demo;

import java.util.Scanner;

public class ScannerDemo {

    private static Scanner scannerOuter = new Scanner(System.in).useDelimiter("\n");

    public static void main(String[] args) {

        new Thread(new InputThread()).start();

        while (true) {

            continue;

        }
    }


    private static class InputThread extends Thread {

        private Scanner scannerInner = new Scanner(System.in).useDelimiter("\n");

        @Override
        public void run(){
            receive();
        }

        public void receive() {

            while (true){
                System.out.println("Please input in the inner: ");
                String inputOuter = scannerInner.nextLine();
                System.out.println(inputOuter);
            }
        }
    }
}
