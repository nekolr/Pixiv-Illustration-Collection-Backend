package dev.cheerfun.pixivic.biz.analysis.tag.mapper;

import dev.cheerfun.pixivic.biz.analysis.tag.po.TrendingTags;
import dev.cheerfun.pixivic.common.po.illust.Tag;
import dev.cheerfun.pixivic.common.util.json.JsonTypeHandler;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TrendingTagsMapper {
    @Select("select * from tags where name=#{keyword} or translated_name=#{keyword}")
    @Results({
            @Result(column = "translated_name", property = "translatedName"),
            @Result(column = "tag_id", property = "id")
    })
    List<Tag> queryTag(String keyword);

    @Insert("insert into trending_tags (trending_tags, date) values (#{tagList,typeHandler=dev.cheerfun.pixivic.common.util.json.JsonTypeHandler},#{date})")
    void insert(String date, List<Tag> tagList);

    @Select("select trending_tags from trending_tags where date=#{date}")
    String queryByDate(String date);
}
