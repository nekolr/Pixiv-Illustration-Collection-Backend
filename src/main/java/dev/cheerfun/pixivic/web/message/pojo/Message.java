package dev.cheerfun.pixivic.web.message.pojo;

import lombok.Data;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/09/23 15:36
 * @description Message
 */
@Data
public class Message {
    private String from;
    private String fromAddr;
    private String to;
    private String toAddr;
    private String type;
    private String title;
    private String content;
    private String url;
}
