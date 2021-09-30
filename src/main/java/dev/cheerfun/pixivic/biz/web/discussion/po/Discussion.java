package dev.cheerfun.pixivic.biz.web.discussion.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import dev.cheerfun.pixivic.basic.sensitive.annotation.SensitiveCheck;
import dev.cheerfun.pixivic.biz.web.collection.po.CollectionTag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/7/29 1:30 下午
 * @description Discussion
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Discussion {
    private Integer id;
    private Integer sectionId;
    @NotBlank
    private String title;
    @NotBlank
    private String content;
    @NotNull
    private Integer userId;
    @NotBlank
    private String username;
    private Integer totalUp;
    private Integer totalDown;
    private Integer totalView;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime createTime;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime updateTime;
    private Integer commentCount;
    private Integer option;
    @SensitiveCheck
    private List<CollectionTag> tagList;
    private Integer bounty;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime latestReplyTime;//按照最后回复时间来排序

    public Discussion(Integer sectionId, String title, String content, Integer userId, String username, List<CollectionTag> tagList, LocalDateTime now) {
        this.sectionId = sectionId;
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.username = username;
        this.tagList = tagList;
        this.createTime = now;
    }
}
