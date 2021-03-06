package dev.cheerfun.pixivic.biz.recommend.mapper;

import dev.cheerfun.pixivic.biz.recommend.domain.UREvent;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

public interface RecommendMapper {

    //根据活跃时间查询用户
    @Select("select user_id from users where last_active_time between #{startDate} and #{endDate}")
    List<Integer> queryUserIdByDateRange(String startDate, String endDate);

    //根据活跃时间查询用户
    @Select("select user_id from users where create_date between #{startDate,typeHandler=org.apache.ibatis.type.LocalDateTimeTypeHandler} and #{endDate,typeHandler=org.apache.ibatis.type.LocalDateTimeTypeHandler}")
    List<Integer> queryUserIdByCreateDateRange(LocalDateTime startDate, LocalDateTime endDate);

    //根据活跃时间查询用户
    @Select("select user_id from users where last_active_time < #{startDate}")
    List<Integer> queryUserIdByDateBefore(String startDate);

    @Select("select * from user_illust_bookmarked  where id>#{bookmarkId} limit 100")
    @Results({
            @Result(property = "eventId", column = "id"),
            @Result(property = "entityId", column = "user_id"),
            @Result(property = "targetEntityId", column = "illust_id"),
            @Result(property = "eventTime", column = "create_date", typeHandler = org.apache.ibatis.type.LocalDateTimeTypeHandler.class),
    })
    List<UREvent> queryBookmarkById(Integer bookmarkId);

    @Select("select * from user_artist_followed  where id>#{followId} limit 100")
    @Results({
            @Result(property = "eventId", column = "id"),
            @Result(property = "entityId", column = "user_id"),
            @Result(property = "targetEntityId", column = "artist_id"),
            @Result(property = "eventTime", column = "create_date", typeHandler = org.apache.ibatis.type.LocalDateTimeTypeHandler.class),
    })
    List<UREvent> queryFollowById(Integer followId);

    @Select("select * from user_illust_bookmarked")
    @Results({
            @Result(property = "eventId", column = "id"),
            @Result(property = "entityId", column = "user_id"),
            @Result(property = "targetEntityId", column = "illust_id"),
            @Result(property = "eventTime", column = "create_date", typeHandler = org.apache.ibatis.type.LocalDateTimeTypeHandler.class),
    })
    List<UREvent> queryAllBookmark();

    @Select("select * from user_artist_followed")
    @Results({
            @Result(property = "eventId", column = "id"),
            @Result(property = "entityId", column = "user_id"),
            @Result(property = "targetEntityId", column = "artist_id"),
            @Result(property = "eventTime", column = "create_date", typeHandler = org.apache.ibatis.type.LocalDateTimeTypeHandler.class),
    })
    List<UREvent> queryAllFollow();
}
