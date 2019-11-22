package dev.cheerfun.pixivic;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

/**
 * @author echo huang
 * @version 1.0
 * @date 2019-06-28 23:05
 * @description
 */
public class RunTest {
    public static void main(String[] args) {
        // define the limit 1 time per 10 minute
        Bandwidth limit = Bandwidth.simple(1, Duration.ofMillis(1));
        // construct the bucket
        Bucket bucket = Bucket4j.builder().addLimit(limit).build();
        ExecutorService executor = Executors.newFixedThreadPool(24);
        IntStream.rangeClosed(1,100)
                .forEach(i -> {
                    executor.submit(() -> {
                        if(bucket.tryConsume(1)){
                            System.out.println(("acquired"));
                        }else{
                            System.out.println("blocked");
                        }
                    });
                });
    }

}
