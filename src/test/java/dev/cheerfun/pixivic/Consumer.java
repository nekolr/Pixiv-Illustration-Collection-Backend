package dev.cheerfun.pixivic;

import com.google.common.collect.Lists;
import io.github.bucket4j.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/3/16 3:01 下午
 * @description Consumer
 */
public class Consumer {
    public static void main(String[] args) throws Exception {
        Bucket bucket = Bucket4j.builder()
                .addLimit(Bandwidth.classic(10, Refill.intervally(10, Duration.ofMinutes(1))))
                .addLimit(Bandwidth.classic(5, Refill.intervally(5, Duration.ofSeconds(20))))
                .build();
        Bucket bucket2 = Bucket4j.builder()
                .addLimit(Bandwidth.classic(10, Refill.intervally(10, Duration.ofMinutes(1))))
                .addLimit(Bandwidth.classic(5, Refill.intervally(5, Duration.ofSeconds(20))))
                .build();
        for (int i = 1; i <= 10; i++) {
            ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
            if (probe.isConsumed()) {
                System.out.println("消费成功");
            } else {
                System.out.println("消费失败");
            }
        }
        System.out.println(bucket.tryConsume(1));
        System.out.println(bucket2.tryConsume(1) + "-");
    }

    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

}

