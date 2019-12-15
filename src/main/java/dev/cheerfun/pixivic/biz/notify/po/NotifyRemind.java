package dev.cheerfun.pixivic.biz.notify.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 19-12-14 下午9:35
 * @description NotifyRemind
 */
@Data
public class NotifyRemind {
    private Integer id;
    private Integer remindId;
    private Integer senderId;
    private String senderName;
    private String senderAction;
    private Integer objectId;
    private String object;
    private String objectType;
    private Integer recipientId;
    private String message;
    private LocalDateTime createDate;
    private String status;
    private LocalDateTime readAt;
}
