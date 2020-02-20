package dev.cheerfun.pixivic.basic.notify.mapper;

import dev.cheerfun.pixivic.basic.notify.po.NotifyBanSetting;
import dev.cheerfun.pixivic.basic.notify.po.NotifySettingConfig;
import dev.cheerfun.pixivic.common.util.json.JsonTypeHandler;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

@Mapper
public interface NotifyMapper {
    @Select("select reply_from from comments where comment_id=#{objectId}")
    Integer queryCommentOwner(Integer objectId);

    @Select("select * from notify_ban_setting where user_id = #{userId}")
    @Results({
            @Result(property = "userId", column = "user_id"),
            @Result(property = "banNotifyActionType", column = "ban_notify_action_type", javaType = List.class, typeHandler = JsonTypeHandler.class),
            @Result(property = "isBanEmail", column = "is_ban_email")
    })
    @Cacheable("userNotifySetting")
    NotifyBanSetting queryUserBanSetting(Integer userId);

    @Select("select * from notify_setting_config")
    @Results({
            @Result(property = "id", column = "notify_setting_config_id"),
            @Result(property = "objectType", column = "object_type"),
            @Result(property = "action", column = "notify_action"),
            @Result(property = "objectRelationship", column = "object_relationship"),
            @Result(property = "messageTemplate", column = "message_template"),
            @Result(property = "notifyChannel", column = "notify_channel", javaType = List.class, typeHandler = JsonTypeHandler.class),
            @Result(property = "description", column = "setting_description"),
            @Result(property = "settingType", column = "setting_type")
    })
    List<NotifySettingConfig> queryNotifySettingConfig();
}
