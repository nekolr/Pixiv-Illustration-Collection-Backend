package dev.cheerfun.pixivic.biz.notify.po;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 19-12-14 下午9:35
 * @description NotifyRemind
 */
@Data
@AllArgsConstructor
public class NotifyRemind {
    private Integer id;
    private Integer senderId;
    private String senderName;
    private String senderAction;
    private Integer objectId;
    private String objectType;
    private Integer recipientId;
    private String message;
    private LocalDateTime createDate;
    private Integer status;
    private LocalDateTime readAt;
}
