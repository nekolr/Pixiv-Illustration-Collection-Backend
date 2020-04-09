package dev.cheerfun.pixivic.biz.web.history.mapper;

import dev.cheerfun.pixivic.biz.web.history.domain.IllustHistory;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.scheduling.annotation.Async;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface IllustHistoryMapper {
    @Insert("insert into illust_history_temp (user_id,illust_id,create_at) value (#{userId},#{illustId},#{createAt,typeHandler=org.apache.ibatis.type.LocalDateTimeTypeHandler})")
    @Async
    Integer insertToTemp(IllustHistory illustHistory);

    @Select("delete from illust_history where create_at < (SELECT DATE_ADD(now(),INTERVAL -3 MONTH));\n" +
            "REPLACE INTO illust_history( `user_id`, `illust_id`, `create_at` ) SELECT user_id,illust_id,create_at FROM illust_history_temp where user_id is not null  order by temp_id;\n" +
            "truncate illust_history_temp;")
    void updateFromTemp();

    @Select("select illust_id from illust_history where user_id=#{userId} and create_at between  #{localDateTime} and (SELECT DATE_ADD(now(),INTERVAL -3 MONTH)) order by create_at desc limit #{currIndex} , #{pageSize}")
    List<Integer> queryByUser(int userId, LocalDateTime localDateTime, int currIndex, int pageSize);
}
