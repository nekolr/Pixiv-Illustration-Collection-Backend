package dev.cheerfun.pixivic.common.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/7/17 1:26 下午
 * @description Email
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Email {
    private String emailAddr;
    private String to;
    private String from;
    private String content;
    private String link;
}
