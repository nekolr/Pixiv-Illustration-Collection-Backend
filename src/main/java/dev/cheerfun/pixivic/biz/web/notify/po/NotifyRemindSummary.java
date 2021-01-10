package dev.cheerfun.pixivic.biz.web.notify.po;

import lombok.Data;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/10/11 12:19 AM
 * @description NotifyRemindSummary
 */
@Data
public class NotifyRemindSummary {
    private Integer type;
    private Integer unread;
    private Integer total;
}
