package dev.cheerfun.pixivic.biz.web.illust.secmapper;

import dev.cheerfun.pixivic.biz.web.illust.po.IllustRelated;
import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.po.illust.ArtistPreView;
import dev.cheerfun.pixivic.common.po.illust.Tag;
import dev.cheerfun.pixivic.common.util.json.JsonTypeHandler;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;


public interface IllustrationBizMapper {
    @Select("select * from illusts where illust_id = #{illustId}")
    @Results({
            @Result(property = "id", column = "illust_id"),
            @Result(property = "artistPreView", column = "artist", javaType = ArtistPreView.class, typeHandler = JsonTypeHandler.class),
            @Result(property = "tools", column = "tools", javaType = List.class, typeHandler = JsonTypeHandler.class),
            @Result(property = "tags", column = "tags", javaType = List.class, typeHandler = JsonTypeHandler.class),
            @Result(property = "imageUrls", column = "image_urls", javaType = List.class, typeHandler = JsonTypeHandler.class),
            @Result(property = "tags", column = "tags", javaType = List.class, typeHandler = JsonTypeHandler.class),
            @Result(property = "artistId", column = "artist_id"),
            @Result(property = "pageCount", column = "page_count"),
            @Result(property = "sanityLevel", column = "sanity_level"),
            @Result(property = "totalBookmarks", column = "total_bookmarks"),
            @Result(property = "totalView", column = "total_view"),
            @Result(property = "xRestrict", column = "x_restrict"),
            @Result(property = "createDate", column = "create_date"),
    })
    Illustration queryIllustrationByIllustId(Integer illustId);

    @Select("  SELECT\n" +
            "          * \n" +
            "     FROM\n" +
            "         `illusts` AS t1\n" +
            "    JOIN (\n" +
            "             SELECT\n" +
            "                   ROUND(\n" +
            "                          RAND( ) * (\n" +
            "                           ( SELECT MIN( illust_id ) FROM (SELECT illust_id FROM `illusts` WHERE illust_id > 1000 AND total_bookmarks > 1000 ORDER BY illust_id desc LIMIT 100) tt ) - ( SELECT MIN( illust_id ) FROM `illusts` WHERE illust_id > 1000 ) \n" +
            "                           ) + ( SELECT MIN( illust_id ) FROM `illusts` WHERE illust_id > 1000 ) \n" +
            "                      ) AS illust_id \n" +
            "         ) AS t2 \n" +
            "WHERE\n" +
            "   t1.illust_id >= t2.illust_id \n" +
            "ORDER BY\n" +
            "    t1.illust_id \n" +
            "LIMIT 2000")
    @Results({
            @Result(property = "id", column = "illust_id"),
            @Result(property = "artistPreView", column = "artist", javaType = ArtistPreView.class, typeHandler = JsonTypeHandler.class),
            @Result(property = "tools", column = "tools", javaType = List.class, typeHandler = JsonTypeHandler.class),
            @Result(property = "tags", column = "tags", javaType = List.class, typeHandler = JsonTypeHandler.class),
            @Result(property = "imageUrls", column = "image_urls", javaType = List.class, typeHandler = JsonTypeHandler.class),
            @Result(property = "tags", column = "tags", javaType = List.class, typeHandler = JsonTypeHandler.class)
    })
    List<Illustration> queryRandomIllustration();

    @Insert({
            "<script>",
            "replace into illust_related (`illust_id`, `related_illust_id`, `order_num`) values ",
            "<foreach collection='illustRelatedList' item='illustRelated' index='index' separator=','>",
            "(#{illustRelated.illustId}, #{illustRelated.relatedIllustId}, #{illustRelated.orderNum})",
            "</foreach>",
            "</script>"
    })
    int insertIllustRelated(@Param("illustRelatedList") List<IllustRelated> illustRelatedList);

    @Select("select * from illusts where update_time >  #{localDateTime,typeHandler=org.apache.ibatis.type.LocalDateTimeTypeHandler}")
    @Results({
            @Result(property = "id", column = "illust_id"),
            @Result(property = "artistPreView", column = "artist", javaType = ArtistPreView.class, typeHandler = JsonTypeHandler.class),
            @Result(property = "tools", column = "tools", javaType = List.class, typeHandler = JsonTypeHandler.class),
            @Result(property = "tags", column = "tags", javaType = List.class, typeHandler = JsonTypeHandler.class),
            @Result(property = "imageUrls", column = "image_urls", javaType = List.class, typeHandler = JsonTypeHandler.class),
            @Result(property = "tags", column = "tags", javaType = List.class, typeHandler = JsonTypeHandler.class)
    })
    List<Illustration> queryRecentIllust(LocalDateTime localDateTime);

    @Select("select illust_id from illusts where update_time >  #{localDateTime,typeHandler=org.apache.ibatis.type.LocalDateTimeTypeHandler}")
    List<Integer> queryRecentIllustId(LocalDateTime localDateTime);

    @Select("select illust_id,create_date from illusts where illust_id between #{from} and #{to}")
    @Results({
            @Result(property = "id", column = "illust_id"),
            @Result(property = "artistPreView", column = "artist", javaType = ArtistPreView.class, typeHandler = JsonTypeHandler.class),
            @Result(property = "tools", column = "tools", javaType = List.class, typeHandler = JsonTypeHandler.class),
            @Result(property = "tags", column = "tags", javaType = List.class, typeHandler = JsonTypeHandler.class),
            @Result(property = "imageUrls", column = "image_urls", javaType = List.class, typeHandler = JsonTypeHandler.class),
            @Result(property = "tags", column = "tags", javaType = List.class, typeHandler = JsonTypeHandler.class)
    })
    List<Illustration> queryIllustInfoForSiteMapById(Integer from, int to);

    @Select("select tags from illusts where illust_id = #{illustId} ")
    @Results({
            @Result(property = "tags", column = "tags", javaType = List.class, typeHandler = JsonTypeHandler.class)
    })
    List<Tag> queryIllustrationTagsById(String illustId);

    @Update("update illusts set tags=#{tags,typeHandler=dev.cheerfun.pixivic.common.util.json.JsonTypeHandler} where illust_id=#{illustId}")
    int updateIllustrationTagsById(String illustId, List<Tag> tags);

    @Update("update illusts set total_bookmarks=total_bookmarks+#{increment}  where illust_id=#{illustId}")
    int updateIllustBookmark(int illustId, int increment);
}
