package dev.cheerfun.pixivic;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RunTest {

    int i = 0;
    volatile int j = 0;

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        System.out.println(134217728 / 1024 / 1024);
        final RunTest runTest = new RunTest();
        long b = System.currentTimeMillis();
        new Thread(() -> {
            while ((System.currentTimeMillis() - b) <= 30) {
                final int k = runTest.i;
                final int v = runTest.j;
                System.out.println("非volatile:" + k);
                System.out.println("volatile:" + v);
            }
        }).start();

        Thread.sleep(15);
        new Thread(() -> {
//            runTest.i=8;
//            runTest.j=20;
            runTest.i = 8;

        }).start();
        Thread.sleep(10);

    }

}
