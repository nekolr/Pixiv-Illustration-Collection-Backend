package dev.cheerfun.pixivic.biz.web.discussion.po;

import dev.cheerfun.pixivic.biz.web.collection.po.CollectionTag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String title;
    private String content;
    private Integer userId;
    private String username;
    private Integer totalUp;
    private Integer totalDown;
    private Integer totalView;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer commentCount;
    private List<CollectionTag> tagList;

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
