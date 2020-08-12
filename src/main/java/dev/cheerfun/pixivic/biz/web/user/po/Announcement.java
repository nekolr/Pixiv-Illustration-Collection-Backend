package dev.cheerfun.pixivic.biz.web.user.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/09/23 15:36
 * @description Announcement
 */
@Data
public class Announcement {
    private Integer id;
    private String title;
    private String content;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createDate;
}
