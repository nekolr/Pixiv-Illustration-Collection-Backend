package dev.cheerfun.pixivic;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.TimeoutException;

public class RunTest {
    public static void main(String[] args) throws IOException, TimeoutException {
        Instant instant = Instant.ofEpochSecond(1585153148);
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        System.out.println(localDateTime);
    }
}
