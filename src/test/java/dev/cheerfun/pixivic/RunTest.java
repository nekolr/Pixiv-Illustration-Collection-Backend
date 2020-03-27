package dev.cheerfun.pixivic;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RunTest {
    public static void main(String[] args) throws IOException, TimeoutException {
        long t1 = System.currentTimeMillis();
        int res = 0;
        final int n = 23333;//放在外面声明 600ms 内部声明80ms
        for (int i = 0; i < 2000000000; i++) {
            //   int n = 23333;//放在外面声明 600ms 内部声明80ms
            res |= n;
        }
        long t2 = System.currentTimeMillis();
        System.out.println(String.format("time: %d, res: %d", t2 - t1, res));
    }
}
