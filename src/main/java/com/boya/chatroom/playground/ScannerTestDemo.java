package com.boya.chatroom.playground;

import java.util.Scanner;

public class ScannerTestDemo {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in).useDelimiter("\n");

        String input = null;
        while (true){
            input = scanner.nextLine();

            if (input.equals("EXIT")){
                break;
            }

            System.out.println("what i insert is: " + input);
        }


    }
}
