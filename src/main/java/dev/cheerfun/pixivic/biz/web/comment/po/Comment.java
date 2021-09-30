package dev.cheerfun.pixivic.biz.web.comment.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import dev.cheerfun.pixivic.basic.sensitive.annotation.SensitiveCheck;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/12/09 20:25
 * @description Comment
 */
@Data
public class Comment {
    private Integer id;
    private String appType;
    private Integer appId;
    private Integer parentId;
    private Integer replyFrom;
    private String replyFromName;
    private Integer replyTo;
    private Integer replyToCommentId;
    private String replyToName;
    private String platform;
    @SensitiveCheck
    @Length(max = 512)
    private String content;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime createDate;
    private Integer likedCount;
    private Boolean isLike = false;
    private List<Comment> subCommentList;

    public LocalDateTime getCreateDate() {
        return createDate == null ? LocalDateTime.now() : createDate;
    }

    public void init(String commentAppType, int commentAppId, int userId) {
        this.appType = commentAppType;
        this.appId = commentAppId;
        this.replyFrom = userId;
        createDate = LocalDateTime.now();
        likedCount = 0;
    }

    public String toStringForQueryLike() {
        return appType + ':' + appId + ":" + id;
    }
}
