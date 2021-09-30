package dev.cheerfun.pixivic.biz.web.admin.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import dev.cheerfun.pixivic.biz.web.admin.util.JpaConverterJson;
import dev.cheerfun.pixivic.biz.web.collection.po.CollectionTag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/7/29 1:30 下午
 * @description Discussion
 */
@Data
@Entity(name = "discussions")
@NoArgsConstructor
@AllArgsConstructor
public class DiscussionPO {
    @Id
    @Column(name = "discussion_id")
    private Integer id;
    @Column(name = "section_id")
    private Integer sectionId;
    private String title;
    private String content;
    @Column(name = "user_id")
    private Integer userId;
    private String username;
    @Column(name = "total_up")
    private Integer totalUp;
    @Column(name = "total_down")
    private Integer totalDown;
    @Column(name = "total_view")
    private Integer totalView;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @Column(name = "create_time")
    private LocalDateTime createTime;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @Column(name = "update_time")
    private LocalDateTime updateTime;
    @Column(name = "comment_count")
    private Integer commentCount;
    @Transient
    private Integer option;
    @Column(name = "tag_list")
    @Convert(converter = JpaConverterJson.class)
    private List<CollectionTag> tagList;

    public DiscussionPO(Integer sectionId, String title, String content, Integer userId, String username, List<CollectionTag> tagList, LocalDateTime now) {
        this.sectionId = sectionId;
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.username = username;
        this.tagList = tagList;
        this.createTime = now;
    }

}
