package dev.cheerfun.pixivic.web.rank.mapper;

import dev.cheerfun.pixivic.web.rank.model.Rank;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/09/12 14:27
 * @description RankMapper
 */
@Mapper
public interface RankMapper {

    @Select("select * from ranks where date = #{date} and mode= #{mode} limit 1")
    @Results({
            @Result(property="date", column="date"),
            @Result(property="mode", column="mode"),
            @Result(property="data", column="data", javaType = List.class,typeHandler=dev.cheerfun.pixivic.common.handler.JsonTypeHandler.class)
    })
     Rank queryByDateAndMode(String date, String mode);
}
