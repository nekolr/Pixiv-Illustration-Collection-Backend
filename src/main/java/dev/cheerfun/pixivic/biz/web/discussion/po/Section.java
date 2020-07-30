package dev.cheerfun.pixivic.biz.web.discussion.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/7/29 1:50 下午
 * @description Section
 */
@Data
public class Section {
    private Integer id;
    private String name;
    private String desc;
    private String slogan;
    private Integer discussionCount;
    private LocalDateTime createTime;
}
