package dev.cheerfun.pixivic.biz.notify.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 19-12-14 下午8:51
 * @description NotifyEvent
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotifyEvent implements Serializable {
   // private Integer id;
    private Integer userId;
    private String action;
    private Integer objectId;
    private String objectType;
    private LocalDateTime createDate;
}
