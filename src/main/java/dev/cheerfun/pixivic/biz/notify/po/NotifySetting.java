package dev.cheerfun.pixivic.biz.notify.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 19-12-14 下午9:33
 * @description NotifySetting
 */
@Data
public class NotifySetting {
    private Integer id;
    private Integer userId;
    private Integer settingId;
    private LocalDateTime createDate;
}
