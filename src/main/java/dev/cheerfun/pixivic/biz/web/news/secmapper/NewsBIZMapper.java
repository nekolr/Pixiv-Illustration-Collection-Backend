package dev.cheerfun.pixivic.biz.web.news.secmapper;

import dev.cheerfun.pixivic.common.po.ACGNew;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface NewsBIZMapper {
    @Select("select new_id,title,intro,author,cover,create_date from news where referer = #{referer}  order by create_date limit #{offset} , #{pageSize}")
    @Results({
            @Result(property = "id", column = "new_id"),
            @Result(property = "createDate", column = "create_date", typeHandler = org.apache.ibatis.type.LocalDateTypeHandler.class),
            @Result(property = "refererUrl", column = "referer_url")
    })
    List<ACGNew> queryByFromAndDate(String referer, int offset, int pageSize);

    @Select("select * from news where new_id = #{newId}")
    @Results({
            @Result(property = "id", column = "new_id"),
            @Result(property = "createDate", column = "create_date", typeHandler = org.apache.ibatis.type.LocalDateTypeHandler.class),
            @Result(property = "refererUrl", column = "referer_url")
    })
    ACGNew queryNewById(Integer newId);
}
