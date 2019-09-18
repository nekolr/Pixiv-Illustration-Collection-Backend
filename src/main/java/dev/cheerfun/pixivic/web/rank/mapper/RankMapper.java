package dev.cheerfun.pixivic.web.rank.mapper;

import dev.cheerfun.pixivic.web.rank.model.Rank;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/09/12 14:27
 * @description RankMapper
 */
@Mapper
public interface RankMapper {
    @Insert("insert into ranks values(#{date}, #{mode},#{data,typeHandler=dev.cheerfun.pixivic.common.handler.JsonTypeHandler})")
    int insert(Rank rank);

    @Select("select * from ranks where date = #{date} and mode= #{mode}")
    public Rank queryByDateAndMode(String data, String mode);
}
