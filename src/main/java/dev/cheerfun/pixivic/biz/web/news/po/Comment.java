package dev.cheerfun.pixivic.biz.web.news.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/10/23 18:39
 * @description Comment
 */
@Data
public class Comment {
    private int userId;
    private int replyTo;
    private String content;
    private LocalDateTime replyDate;
}
