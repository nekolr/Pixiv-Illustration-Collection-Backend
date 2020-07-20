package dev.cheerfun.pixivic.biz.credit.mapper;

import dev.cheerfun.pixivic.biz.credit.po.CreditConfig;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface CreditMapper {
    @Select("")
    List<CreditConfig> queryCreditConfig();

    @Select("")
    Integer queryCreditCount(Integer userId, String objectType, String action);

    @Insert("")
    Integer insertCreditLog();

    @Update("")
    Integer increaseUserScore(Integer userId, Integer score);
}
