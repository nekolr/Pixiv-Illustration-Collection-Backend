package dev.cheerfun.pixivic.basic.notify.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 19-12-14 下午8:51
 * @description NotifyEvent
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotifyEvent {
    private Integer userId;
    private String action;
    private Integer objectId;
    private String objectType;
    private LocalDateTime createDate;
}
