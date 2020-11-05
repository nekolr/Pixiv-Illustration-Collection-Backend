package dev.cheerfun.pixivic.biz.web.user.mapper;

import dev.cheerfun.pixivic.biz.credit.po.CreditHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

//@Mapper
public interface UserCreditMapper {
    @Select("select credit_log_id from user_credit_log where user_id=#{userId} order by credit_log_id desc  limit 1000")
    List<Integer> queryRecentlyCreditHistoryList(int userId);

    @Select("select count(*) from user_credit_log where user_id=#{userId}")
    Integer queryCreditHistoryCount(int userId);

    @Select("select * from user_credit_log where credit_log_id=#{creditHistoryId}")
    @Results({
            @Result(property = "id", column = "credit_log_id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "objectType", column = "object_type"),
            @Result(property = "objectId", column = "object_id"),
            @Result(property = "action", column = "action"),
            @Result(property = "creditOption", column = "credit_option"),
            @Result(property = "creditScore", column = "credit_score"),
            @Result(property = "creditDesc", column = "credit_desc"),
            @Result(property = "createTime", column = "create_time", typeHandler = org.apache.ibatis.type.LocalDateTimeTypeHandler.class)
    })
    CreditHistory queryCreditHistoryById(Integer creditHistoryId);
}
