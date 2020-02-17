package dev.cheerfun.pixivic.basic.notify.po;

import lombok.Data;

import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 19-12-14 下午9:33
 * @description NotifySetting
 */
@Data
public class NotifyBanSetting {
    private Integer id;
    private Integer userId;
    private List<String> banNotifyActionType;//屏蔽通知事件类型
    private Integer isBanEmail;
}
