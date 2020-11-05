package dev.cheerfun.pixivic.biz.forum.mapper;

import dev.cheerfun.pixivic.biz.web.common.po.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

//@Mapper
public interface SyncAccountMapper {
    @Select("select user_id from forum_account_sync_log where forum_account_sync_log_id = 1")
    Integer queryLatestSyncUserId();

    @Update("update forum_account_sync_log set user_id = #{userId} where forum_account_sync_log_id = 1")
    void updateLatestSyncUserId(Integer userId);

    @Insert("INSERT INTO forum_account_sync_log (user_id, sync_status, description, create_time) VALUES (#{userId}, 0, #{log}, now());")
    void logError(Integer userId, String log);

    @Select({
            "SELECT * FROM users WHERE user_id>#{userId} limit 50",
    })
    @Results({
            @Result(property = "id", column = "user_id"),
            @Result(property = "isBan", column = "is_ban"),
            @Result(property = "permissionLevel", column = "permission_level"),
            @Result(property = "pixivAccount", column = "pixiv_account"),
            @Result(property = "pixivPassword", column = "pixiv_password"),
            @Result(property = "qqOpenId", column = "qq_open_id"),
            @Result(property = "isCheckEmail", column = "is_check_email"),
            @Result(property = "createDate", column = "create_date", typeHandler = org.apache.ibatis.type.LocalDateTimeTypeHandler.class),
            @Result(property = "updateDate", column = "update_date", typeHandler = org.apache.ibatis.type.LocalDateTimeTypeHandler.class)
    })
    List<User> queryUser(Integer userId);
}
