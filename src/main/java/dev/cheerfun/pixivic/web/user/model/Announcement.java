package dev.cheerfun.pixivic.web.user.model;

import lombok.Data;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/09/23 15:36
 * @description Announcement
 */
@Data
public class Announcement {
    private int announcementId;
    private String title;
    private String content;
    private String date;
    private int isPop;//0\1
}
