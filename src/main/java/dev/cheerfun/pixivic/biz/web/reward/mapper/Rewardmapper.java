package dev.cheerfun.pixivic.biz.web.reward.mapper;

import dev.cheerfun.pixivic.biz.credit.po.CreditHistory;
import dev.cheerfun.pixivic.biz.web.reward.po.Reward;
import org.apache.ibatis.annotations.*;

import java.util.List;

//@Mapper
public interface Rewardmapper {
    @Insert("insert into rewards (reward_app_type,reward_app_id,reward_from,reward_from_username,reward_to,price,create_date) values (#{appType},#{appId},#{from},#{fromName},#{to},#{price},#{createDate,typeHandler=org.apache.ibatis.type.LocalDateTimeTypeHandler})")
    void insertReward(Reward reward);

    @Update("INSERT INTO reward_summary (reward_app_type, reward_app_id, total, reward_count)\n" +
            "            VALUES (#{appType},#{appId},#{price},1)\n" +
            "            ON DUPLICATE KEY UPDATE \n" +
            "            total = total +  #{price},reward_count=reward_count+1")
    void updateSummary(Reward reward);

    @Select("select reward_id from rewards where reward_app_type = #{appType} and reward_app_id = #{appId} order by reward_id")
    @Results({
            @Result(property = "id", column = "reward_id"),
            @Result(property = "appType", column = "reward_app_type"),
            @Result(property = "appId", column = "reward_app_id"),
            @Result(property = "from", column = "reward_from"),
            @Result(property = "fromName", column = "reward_from_username"),
            @Result(property = "to", column = "reward_to"),
            @Result(property = "createDate", column = "create_date", typeHandler = org.apache.ibatis.type.LocalDateTimeTypeHandler.class)
    })
    List<Integer> pullReward(String appType, int appId);

    @Select("select * from rewards where reward_id = #{rewardId}")
    @Results({
            @Result(property = "id", column = "reward_id"),
            @Result(property = "appType", column = "reward_app_type"),
            @Result(property = "appId", column = "reward_app_id"),
            @Result(property = "from", column = "reward_from"),
            @Result(property = "fromName", column = "reward_from_username"),
            @Result(property = "to", column = "reward_to"),
            @Result(property = "createDate", column = "create_date", typeHandler = org.apache.ibatis.type.LocalDateTimeTypeHandler.class)
    })
    Reward queryRewardById(Integer rewardId);

    @Select("select reward_count from reward_summary where reward_app_type = #{appType} and reward_app_id = #{appId} ")
    Integer pullRewardCount(String appType, int appId);

    @Insert("insert into user_credit_log (user_id,object_type,object_id,`action`,credit_option,credit_score,credit_desc) values(#{userId},#{objectType},#{objectId},#{action},#{creditOption},#{creditScore},#{creditDesc})")
    Integer insertCreditLog(CreditHistory creditHistory);

}
