package dev.cheerfun.pixivic.biz.web.discussion.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import dev.cheerfun.pixivic.biz.web.discussion.po.Discussion;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/7/30 4:54 下午
 * @description DiscussionVO
 */
@Data
public class DiscussionVO {
    private Integer id;
    private Integer sectionId;
    private String title;
    private Integer userId;
    private String username;
    private Integer totalUp;
    private Integer totalDown;
    private Integer totalView;
    private Integer commentCount;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime createTime;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime updateTime;

    public DiscussionVO(Discussion discussion, Integer commentCount) {
        this.id = discussion.getId();
        this.sectionId = discussion.getSectionId();
        this.title = discussion.getTitle();
        this.userId = discussion.getUserId();
        this.username = discussion.getUsername();
        this.totalUp = discussion.getTotalUp();
        this.totalDown = discussion.getTotalDown();
        this.totalView = discussion.getTotalView();
        this.commentCount = commentCount;
        this.createTime = discussion.getCreateTime();
        this.updateTime = discussion.getUpdateTime();

    }

    public DiscussionVO(Integer id, Integer sectionId, String title, Integer userId, String username, Integer totalUp, Integer totalDown, Integer totalView, Integer commentCount, LocalDateTime createTime, LocalDateTime updateTime) {
        this.id = id;
        this.sectionId = sectionId;
        this.title = title;
        this.userId = userId;
        this.username = username;
        this.totalUp = totalUp;
        this.totalDown = totalDown;
        this.totalView = totalView;
        this.commentCount = commentCount;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public DiscussionVO() {
    }
}
