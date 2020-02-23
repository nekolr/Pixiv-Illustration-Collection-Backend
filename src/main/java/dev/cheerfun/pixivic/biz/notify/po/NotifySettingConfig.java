package dev.cheerfun.pixivic.biz.notify.po;

import lombok.Data;

import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 19-12-14 下午9:23
 * @description NotifySettingConfig
 */
@Data
public class NotifySettingConfig {
    private Integer id;
    private String objectType;
    private String action;
    private String objectRelationship;
    private String messageTemplate;
    private List<String> notifyChannel;
    private String description;
    private String settingType;
}