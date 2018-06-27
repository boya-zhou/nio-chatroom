package com.boya.chatroom.playground;

import com.boya.chatroom.client.Client;
import com.boya.chatroom.clienttest.ClientTest;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientTestStarter {

    public static void main(String[] args) {

        CyclicBarrier barrier = new CyclicBarrier(30 + 1);
        ExecutorService executorService = Executors.newFixedThreadPool(300);

        for (int i = 0; i < 30; i++) {
            executorService.execute(new ClientTest(barrier));
        }

        try {
            barrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }

        System.out.println("Testing now begin");
    }
}
