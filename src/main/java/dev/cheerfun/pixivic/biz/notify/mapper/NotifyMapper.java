package dev.cheerfun.pixivic.biz.notify.mapper;

import dev.cheerfun.pixivic.biz.notify.po.NotifyBanSetting;
import dev.cheerfun.pixivic.biz.notify.po.NotifyRemind;
import dev.cheerfun.pixivic.biz.notify.po.NotifySettingConfig;
import dev.cheerfun.pixivic.common.util.json.JsonTypeHandler;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface NotifyMapper {

    @Insert("insert into notify_remind (remind_type,remind_actors,actor_count,object_id,object_type,object_title,recipient_id,message,extend,create_date) value (#{type},#{actors,typeHandler=dev.cheerfun.pixivic.common.util.json.JsonTypeHandler},#{actorCount},#{objectId},#{objectType},#{objectTitle},#{recipientId},#{message},#{extend},#{createDate,typeHandler=org.apache.ibatis.type.LocalDateTimeTypeHandler})")
    Integer insertNotifyRemind(NotifyRemind notifyRemind);

    @Select("select * from notify_ban_setting where user_id = #{userId}")
    @Results({
            @Result(property = "userId", column = "user_id"),
            @Result(property = "banNotifyActionType", column = "ban_notify_action_type", javaType = List.class, typeHandler = JsonTypeHandler.class),
            @Result(property = "isBanEmail", column = "is_ban_email")
    })
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

    @Select("select remind_id from notify_remind where recipient_id=#{recipientId} and remind_type=#{type} and create_date >=#{localDateTime,typeHandler=org.apache.ibatis.type.LocalDateTimeTypeHandler} order by create_date desc")
    List<Integer> queryRecentlyRemind(Integer recipientId, Integer type, LocalDateTime localDateTime);

    @Select("select * from notify_remind where remind_id =#{#remindId}")
    @Results({
            @Result(property = "id", column = "remind_id"),
            @Result(property = "type", column = "remind_type"),
            @Result(property = "recipientId", column = "recipient_id"),
            @Result(property = "actors", column = "remind_actors", javaType = List.class, typeHandler = JsonTypeHandler.class),
            @Result(property = "actorCount", column = "actor_count"),
            @Result(property = "objectId", column = "object_id"),
            @Result(property = "objectType", column = "object_type"),
            @Result(property = "objectTitle", column = "object_title"),
            @Result(property = "createDate", column = "create_date", typeHandler = org.apache.ibatis.type.LocalDateTimeTypeHandler.class),
            @Result(property = "status", column = "read_status")
    })
    NotifyRemind queryNotifyRemindById(Integer remindId);

    @Update("update notify_remind set remind_actors=#{actors,typeHandler=dev.cheerfun.pixivic.common.util.json.JsonTypeHandler},actor_count=#{actorCount} , create_date=#{createDate} where remind_id=#{id}")
    void updateRemindActorAndCreateDate(NotifyRemind notifyRemind);

    @Insert("INSERT INTO notify_remind_summary (user_id,remind_type,unread_count,total_count)\n" +
            "VALUES (#{recipientId},#{type},1,1)\n" +
            "ON DUPLICATE KEY UPDATE\n" +
            "unread_count = unread_count + 1,total_count=total_count+1")
    void updateRemindSummary(NotifyRemind notifyRemind);
}
