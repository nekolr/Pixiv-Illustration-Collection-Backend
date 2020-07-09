package dev.cheerfun.pixivic.biz.recommend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RecommendMapper {

    //根据活跃时间查询用户
    @Select("select user_id from users where last_active_time between #{startDate} and #{endDate}")
    List<Integer> queryUserIdByDateRange(String startDate, String endDate);

}
