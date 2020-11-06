package dev.cheerfun.pixivic.biz.analysis.tag.secmapper;

import dev.cheerfun.pixivic.common.po.illust.Tag;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface TagMapper {

    @Select("select * from tags where name=#{keyword} or translated_name=#{keyword}")
    @Results({
            @Result(column = "translated_name", property = "translatedName"),
            @Result(column = "tag_id", property = "id")
    })
    List<Tag> queryTag(String keyword);

}
