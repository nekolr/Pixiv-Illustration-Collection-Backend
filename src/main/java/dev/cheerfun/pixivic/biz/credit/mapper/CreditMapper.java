package dev.cheerfun.pixivic.biz.credit.mapper;

import dev.cheerfun.pixivic.biz.credit.po.CreditConfig;
import dev.cheerfun.pixivic.biz.credit.po.CreditHistory;
import dev.cheerfun.pixivic.common.util.json.JsonTypeHandler;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CreditMapper {
    @Select("select * from user_credit_config")
    @Results({
            @Result(property = "id", column = "config_id"),
            @Result(property = "objectType", column = "object_type"),
            @Result(property = "limitNum", column = "limit_num"),
            @Result(property = "isRandom", column = "is_random"),
            @Result(property = "randomStart", column = "random_start"),
            @Result(property = "randomEnd", column = "random_end"),
            @Result(property = "desc", column = "config_desc"),
    })
    List<CreditConfig> queryCreditConfig();

    @Select("select count(*) from user_credit_log where user_id =#{userId} and object_type=#{objectType} and action=#{objectType} and to_days(create_time) = to_days(now());")
    Integer queryCreditCount(Integer userId, String objectType, String action);

    @Insert("insert into user_credit_log (user_id,object_type,object_id,`action`,credit_option,credit_score,credit_desc) values(#{creditHistory.userId},#{creditHistory.objectType},#{creditHistory.objectId},#{creditHistory.action},#{creditHistory.creditOption},#{creditHistory.creditScore},#{creditHistory.creditDesc})")
    Integer insertCreditLog(CreditHistory creditHistory);

    @Update("update users set star=star+#{score} where user_id=#{userId}")
    Integer increaseUserScore(Integer userId, Integer score);

    @Update("update users set star=star-#{score} where user_id=#{userId}")
    Integer decreaseUserScore(Integer userId, Integer score);

    @Select("select star from users where user_id=#{userId}")
    Integer queryUserScore(Integer userId);
}
