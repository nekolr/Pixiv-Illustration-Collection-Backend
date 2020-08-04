package dev.cheerfun.pixivic.biz.web.admin.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import dev.cheerfun.pixivic.basic.sensitive.annotation.SensitiveCheck;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Max;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/12/09 20:25
 * @description Comment
 */
@Data
@Entity
@Table(name = "comments")
public class CommentPO {
    @Column(name = "app_type")
    private String appType;
    @Column(name = "app_id")
    private Integer appId;
    @Id
    @Column(name = "comment_id")
    private Integer id;
    @Column(name = "parent_id")
    private Integer parentId;
    @Column(name = "reply_from")
    private Integer replyFrom;
    @Column(name = "reply_from_name")
    private String replyFromName;
    @Column(name = "reply_to")
    private Integer replyTo;
    @Column(name = "reply_to_name")
    private String replyToName;
    private String platform;
    private String content;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @Column(name = "create_date")
    private LocalDateTime createDate;
    @Column(name = "liked_count")
    private Integer likedCount;
    @Column(name = "is_like")
    private Boolean isLike = false;
    @Transient
    private List<CommentPO> subCommentList;

}
