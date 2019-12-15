package dev.cheerfun.pixivic.biz.notify.po;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 19-12-14 下午8:51
 * @description NotifyEvent
 */
@Data
public class NotifyEvent {
    private Integer id;
    private Integer userId;
    private String action;
    private Integer objectId;
    private String objectType;
    private LocalDateTime createDate;
}
