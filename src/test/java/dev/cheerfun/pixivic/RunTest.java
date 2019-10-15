package dev.cheerfun.pixivic;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * @author echo huang
 * @version 1.0
 * @date 2019-06-28 23:05
 * @description
 */
public class RunTest {
    public static void main(String[] args) {
        LocalTime from = LocalTime.now().plusMinutes(-5);
        LocalTime to = LocalTime.now().plusMinutes(15);

        System.out.println(from.format(DateTimeFormatter.ISO_LOCAL_TIME));
    }
}
