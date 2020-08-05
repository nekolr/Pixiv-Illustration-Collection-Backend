package dev.cheerfun.pixivic.biz.web.admin.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import dev.cheerfun.pixivic.biz.web.admin.util.JpaConverterJson;
import dev.cheerfun.pixivic.biz.web.collection.po.CollectionTag;
import dev.cheerfun.pixivic.common.po.Illustration;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/4/29 5:22 下午
 * @description Collection
 */
@Data
@Entity(name = "collections")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CollectionPO {
    @Id
    @Column(name = "collection_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column(name = "user_id")
    private Integer userId;
    private String username;
    @Convert(converter = JpaConverterJson.class)
    @Transient
    private Illustration cover;
    private String title;
    private String caption;
    @Column(name = "tag_list")
    @Convert(converter = JpaConverterJson.class)
    private List<CollectionTag> tagList;
    @Column(name = "illust_count")
    private Integer illustCount;
    @Transient
    private List<Illustration> illustrationList;
    @Column(name = "is_public")
    private Integer isPublic;
    @Column(name = "use_flag")
    private Integer useFlag;
    @Column(name = "forbid_comment")
    private Integer forbidComment;
    @Column(name = "porn_warning")
    private Integer pornWarning;
    @Column(name = "total_bookmarked")
    private Integer totalBookmarked;
    @Column(name = "total_view")
    private Integer totalView;
    @Column(name = "total_people_seen")
    private Integer totalPeopleSeen;
    @Column(name = "total_liked")
    private Integer totalLiked;
    @Column(name = "total_reward")
    private Integer totalReward;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @Column(name = "create_time")
    private LocalDateTime createTime;

}
