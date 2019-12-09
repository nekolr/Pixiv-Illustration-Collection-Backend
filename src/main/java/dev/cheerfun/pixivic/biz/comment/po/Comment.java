package dev.cheerfun.pixivic.biz.comment.po;

import lombok.Data;

import java.time.LocalDate;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/12/09 20:25
 * @description Comment
 */
@Data
public class Comment {
    private String appType;
    private Integer appId;
    private Integer id;
    private Integer parentId;
    private Integer userId;
    private Integer replyTo;
    private String content;
    private LocalDate createDate;
    private Integer likedCount;
    private Boolean isLike;

    public LocalDate getCreateDate() {
        return createDate == null ? LocalDate.now() : createDate;
    }
}
