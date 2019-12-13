package dev.cheerfun.pixivic.biz.comment.po;

import dev.cheerfun.pixivic.basic.sensitive.annotation.SensitiveCheck;
import lombok.Data;

import java.time.LocalDateTime;

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
    private Integer from;
    private Integer replyTo;
    @SensitiveCheck
    private String content;
    private LocalDateTime createDate;
    private Integer likedCount;
    private Boolean isLike=false;

    public LocalDateTime getCreateDate() {
        return createDate == null ? LocalDateTime.now() : createDate;
    }

    public void init(String commentAppType, int commentAppId, int userId) {
        this.appType = commentAppType;
        this.appId = commentAppId;
        this.from = userId;
    }

    public String toStringForQueryLike() {
        return appType + ':' + appId + ":" + id;
    }
}
