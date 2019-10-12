package dev.cheerfun.pixivic.crawler.pixiv.mapper;

import dev.cheerfun.pixivic.common.model.Spotlight;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SpotlightMapper {
    @Insert("<script>" +
            "REPLACE  INTO " +
            "spotlights (`spotlight_id`, `title`, `pure_title`, `thumbnail`, `article_url`, `publish_date`, `category`, `subcategory_label`)" +
            "VALUES" +
            "<foreach collection='spotlights' item='spotlight' index='index'  separator=','>" +
            "(#{spotlight.id}," +
            "#{spotlight.title}," +
            "#{spotlight.pureTitle}," +
            "#{spotlight.thumbnail}," +
            "#{spotlight.articleUrl}," +
            "#{spotlight.publishDate}," +
            "#{spotlight.category}," +
            "#{spotlight.subcategoryLabel})" +
            "</foreach>" +
            "</script>")
    int insert(@Param("spotlights") List<Spotlight> spotlights);

    @Insert({
            "<script>",
            "insert IGNORE into spotlight_illust_relation values ",
            "<foreach collection='illustIds' item='illustId' index='index' separator=','>",
            "(#{spotlightId}, #{illustId})",
            "</foreach>",
            "</script>"
    })
    int insertRelation(int spotlightId, List<Integer> illustIds);
}
