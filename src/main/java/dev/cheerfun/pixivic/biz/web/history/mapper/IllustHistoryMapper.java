package dev.cheerfun.pixivic.biz.web.history.mapper;

import dev.cheerfun.pixivic.biz.web.history.domain.IllustHistory;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.scheduling.annotation.Async;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface IllustHistoryMapper {
    @Insert("insert into illust_history_temp (user_id,illust_id,create_at) value (#{userId},#{illustId},#{createAt,typeHandler=org.apache.ibatis.type.LocalDateTimeTypeHandler})")
    Integer insertToTemp(IllustHistory illustHistory);

    @Delete("delete from illust_history where create_at < (SELECT DATE_ADD(now(),INTERVAL -6 MONTH))")
    Integer deleteIllustHistory();

    @Delete("delete from illust_history where user_id =#{userId}")
    Integer deleteIllustHistorByUserId(Integer userId);

    @Select("REPLACE INTO illust_history( `user_id`, `illust_id`, `create_at` ) SELECT user_id,illust_id,create_at FROM illust_history_temp where user_id is not null  order by temp_id")
    void tempToIllustHistory();

    @Delete("truncate illust_history_temp")
    void truncateTemp();

    @Select("select illust_id from illust_history where user_id=#{userId} and create_at between (SELECT DATE_ADD(now(),INTERVAL -3 MONTH)) and #{localDateTime} order by create_at desc limit #{currIndex} , #{pageSize}")
    List<Integer> queryByUser(int userId, LocalDateTime localDateTime, int currIndex, int pageSize);
}
