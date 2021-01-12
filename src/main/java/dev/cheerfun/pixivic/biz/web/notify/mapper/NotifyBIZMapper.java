package dev.cheerfun.pixivic.biz.web.notify.mapper;

import dev.cheerfun.pixivic.biz.web.notify.po.NotifyRemindSummary;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface NotifyBIZMapper {
    @Select("select remind_id from notify_remind where recipient_id=#{userId} and remind_type=#{type} and create_date<FROM_UNIXTIME(#{offset}) order by create_date desc limit #{pageSize} ")
    List<Integer> queryRemind(int userId, Integer type, long offset, int pageSize);

    @Update("update notify_remind set read_status=1 where remind_id=#{remindId}")
    void readRemind(Integer remindId);

    @Update("update notify_remind set read_status=1 where recipient_id=#{userId} and  remind_type=#{type}")
    void readAllRemindByType(int userId, Integer type);

    @Select("select remind_type,unread_count,total_count from notify_remind_summary where user_id=#{userId}")
    @Results({
            @Result(property = "type", column = "remind_type"),
            @Result(property = "unread", column = "unread_count"),
            @Result(property = "total", column = "total_count")
    })
    List<NotifyRemindSummary> queryRemindSummary(int userId);

    @Update("update notify_remind_summary set unread_count=(select count(*) from notify_remind where recipient_id=#{userId} and remind_type=#{type} and read_status=0) where user_id=#{userId} and remind_type=#{type}")
    void updateRemindSummary(Integer userId, Integer type);

    @Update("update notify_remind_summary set unread_count=0 where user_id=#{userId} and remind_type=#{type}")
    void updateRemindSummaryToZeroByType(Integer userId, Integer type);
}
