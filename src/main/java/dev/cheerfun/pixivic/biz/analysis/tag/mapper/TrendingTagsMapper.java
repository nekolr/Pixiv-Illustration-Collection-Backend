package dev.cheerfun.pixivic.biz.analysis.tag.mapper;

import dev.cheerfun.pixivic.common.po.illust.Tag;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface TrendingTagsMapper {
    @Insert("replace into trending_tags (trending_tags, date) values (#{tagList,typeHandler=dev.cheerfun.pixivic.common.util.json.JsonTypeHandler},#{date})")
    void insert(String date, List<Tag> tagList);

    @Select("select trending_tags from trending_tags where date=#{date}")
    String queryByDate(String date);

    @Update("update trending_tags set date=#{date} where date=#{old} ")
    void replace(String date, String old);
}
