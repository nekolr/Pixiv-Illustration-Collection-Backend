package dev.cheerfun.pixivic.biz.credit.mapper;

import dev.cheerfun.pixivic.biz.credit.po.CreditConfig;
import dev.cheerfun.pixivic.biz.credit.po.CreditHistory;
import dev.cheerfun.pixivic.common.util.json.JsonTypeHandler;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
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

    @Select("select create_time from user_credit_log where user_id =#{userId} and object_type=#{objectType} and action=#{action}  order by create_time desc limit #{limitNum},1")
    @Results({
            @Result(javaType = LocalDateTime.class, column = "create_time", typeHandler = org.apache.ibatis.type.LocalDateTimeTypeHandler.class)
    })
    LocalDateTime queryCreditCount(Integer userId, String objectType, String action, Integer limitNum);

    @Insert("insert into user_credit_log (user_id,object_type,object_id,`action`,credit_option,credit_score,credit_desc) values(#{userId},#{objectType},#{objectId},#{action},#{creditOption},#{creditScore},#{creditDesc})")
    Integer insertCreditLog(CreditHistory creditHistory);

    @Update("update users set star=star+#{score} where user_id=#{userId}")
    Integer increaseUserScore(Integer userId, Integer score);

    @Update("update users set star=star-#{score} where user_id=#{userId}")
    Integer decreaseUserScore(Integer userId, Integer score);

    @Select("select star from users where user_id=#{userId}")
    Integer queryUserScore(Integer userId);
}
