package dev.cheerfun.pixivic.common.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2021/1/29 1:50 PM
 * @description Message
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private String phone;
    private String content;
}
