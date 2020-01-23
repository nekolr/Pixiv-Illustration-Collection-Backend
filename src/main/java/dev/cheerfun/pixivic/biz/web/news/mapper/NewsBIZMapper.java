package dev.cheerfun.pixivic.biz.web.news.mapper;

import dev.cheerfun.pixivic.common.po.ACGNew;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface NewsBIZMapper {
    @Select("select * from news where referer = #{referer}  order by create_date limit #{offset} , #{pageSize}")
    @Results({
            @Result(property = "id", column = "new_id"),
            @Result(property = "createDate", column = "create_date",typeHandler=org.apache.ibatis.type.LocalDateTypeHandler.class),
            @Result(property = "refererUrl", column = "referer_url")
    })
    List<ACGNew> queryByFromAndDate(String referer, int offset, int pageSize);
}
