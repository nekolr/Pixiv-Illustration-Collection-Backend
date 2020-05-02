package dev.cheerfun.pixivic.biz.web.collection.po;

import dev.cheerfun.pixivic.common.po.Illustration;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/4/29 5:22 下午
 * @description Collection
 */
@Data
public class Collection {
    private Integer id;
    private Integer userId;
    private String username;
    private Illustration cover;
    private String title;
    private String caption;
    private List<CollectionTag> tagList;
    private Integer illustCount;
    private List<Illustration> illustrationList;
    private Integer isPublic;
    private Integer useFlag;
    private Integer forbidComment;
    private Integer pornWarning;
    private Integer totalBookmarked;
    private Integer totalView;
    private Integer totalLiked;
    private Integer totalReward;
    private LocalDateTime createTime;
    private Integer updateTime;
}
