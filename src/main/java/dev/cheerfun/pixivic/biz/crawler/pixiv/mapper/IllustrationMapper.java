package dev.cheerfun.pixivic.biz.crawler.pixiv.mapper;

import dev.cheerfun.pixivic.biz.web.rank.po.Rank;
import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.po.illust.Tag;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/09/12 14:27
 * @description IllustrationMapper
 */
@Mapper
public interface IllustrationMapper {
    @Insert("<script>" +
            "REPLACE  INTO " +
            "illusts (`illust_id`, `title`, `type`, `caption`, `restrict`, `artist`, `tools`, `tags`, `create_date`, `page_count`, `width`, `height`, `sanity_level`, `x_restrict`, `total_bookmarks`, `total_view`, `image_urls`, `artist_id`)" +
            "VALUES" +
            "<foreach collection='illustrations' item='illustration' index='index'  separator=','>" +
            "(#{illustration.id}," +
            "#{illustration.title}," +
            "#{illustration.type}," +
            "#{illustration.caption}," +
            "#{illustration.restrict}," +
            "#{illustration.artistPreView,typeHandler=dev.cheerfun.pixivic.common.util.json.JsonTypeHandler}," +
            "#{illustration.tools,typeHandler=dev.cheerfun.pixivic.common.util.json.JsonTypeHandler}," +
            "#{illustration.tags,typeHandler=dev.cheerfun.pixivic.common.util.json.JsonTypeHandler}," +
            "#{illustration.createDate}," +
            "#{illustration.pageCount}," +
            "#{illustration.width}," +
            "#{illustration.height}," +
            "#{illustration.sanityLevel}," +
            "#{illustration.xRestrict}," +
            "#{illustration.totalBookmarks}," +
            "#{illustration.totalView}," +
            "#{illustration.imageUrls,typeHandler=dev.cheerfun.pixivic.common.util.json.JsonTypeHandler}," +
            "#{illustration.artistId})" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("illustrations") List<Illustration> illustrations);

    @Insert("<script>" +
            "REPLACE  INTO " +
            "illusts (`illust_id`, `title`, `type`, `caption`, `restrict`, `artist`, `tools`, `tags`, `create_date`, `page_count`, `width`, `height`, `sanity_level`, `x_restrict`, `total_bookmarks`, `total_view`, `image_urls`, `artist_id`)" +
            "VALUES" +
            "(#{id}," +
            "#{title}," +
            "#{type}," +
            "#{caption}," +
            "#{restrict}," +
            "#{artistPreView,typeHandler=dev.cheerfun.pixivic.common.util.json.JsonTypeHandler}," +
            "#{tools,typeHandler=dev.cheerfun.pixivic.common.util.json.JsonTypeHandler}," +
            "#{tags,typeHandler=dev.cheerfun.pixivic.common.util.json.JsonTypeHandler}," +
            "#{createDate}," +
            "#{pageCount}," +
            "#{width}," +
            "#{height}," +
            "#{sanityLevel}," +
            "#{xRestrict}," +
            "#{totalBookmarks}," +
            "#{totalView}," +
            "#{imageUrls,typeHandler=dev.cheerfun.pixivic.common.util.json.JsonTypeHandler}," +
            "#{artistId})" +
            "</script>")
    int simpleInsert(Illustration illustration);

    @Update("FLUSH TABLES")
    int flush();

    @Insert({
            "<script>",
            "insert IGNORE into tags(name, translated_name) values ",
            "<foreach collection='tags' item='tag' index='index' separator=','>",
            "(#{tag.name}, #{tag.translatedName})",
            "</foreach>",
            "</script>"
    })
    int insertTag(@Param("tags") List<Tag> tags);

    @Insert({
            "<script>",
            "insert IGNORE into tag_illust_relation values",
            "<foreach collection='illustrations' item='illustration' index='index' separator=','>",
            "<foreach collection='illustration.tags' item='tag' index='index' separator=','>",
            "(" +
                    "(SELECT tag_id FROM tags WHERE NAME = #{tag.name} AND translated_name = #{tag.translatedName})" +
                    ",#{illustration.id})",
            "</foreach>",
            "</foreach>",
            "</script>"
    })
    int insertTagIllustRelation(@Param("illustrations") List<Illustration> illustrations);

    @Select({
            "SELECT tag_id FROM tags WHERE NAME = #{name} AND translated_name = #{translatedName}"
    })
    Long getTagId(String name, String translatedName);

    @Select({
            "<script>",
            "SELECT temp.illust_id FROM (",
            "<foreach collection='illustIds' item='illustId' index='index' separator='UNION ALL'>",
            "(SELECT #{illustId} AS illust_id )",
            "</foreach>",
            ") as temp WHERE temp.illust_id  NOT IN (SELECT illusts.illust_id FROM illusts)",
            "</script>"
    })
    List<Integer> queryIllustsNotInDb(@Param("illustIds") List<Integer> illustIds);

    @Insert("insert into ranks(`mode`,`date`,`data`) values(#{mode}, #{date},#{data,typeHandler=dev.cheerfun.pixivic.common.util.json.JsonTypeHandler})")
    int insertRank(Rank rank);
}
