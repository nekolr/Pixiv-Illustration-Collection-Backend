package dev.cheerfun.pixivic.web.illust.mapper;

import dev.cheerfun.pixivic.common.model.Artist;
import dev.cheerfun.pixivic.common.model.Illustration;
import dev.cheerfun.pixivic.common.model.illust.ArtistPreView;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface IllustrationBizMapper {
    @Select("select * from illusts where illust_id = #{illustId} ")
    @Results({
            @Result(property = "id", column = "illust_id"),
            @Result(property = "artistPreView", column = "artist", javaType = ArtistPreView.class, typeHandler = dev.cheerfun.pixivic.common.handler.JsonTypeHandler.class),
            @Result(property = "tools", column = "tools", javaType = List.class, typeHandler = dev.cheerfun.pixivic.common.handler.JsonTypeHandler.class),
            @Result(property = "tags", column = "tags", javaType = List.class, typeHandler = dev.cheerfun.pixivic.common.handler.JsonTypeHandler.class),
            @Result(property = "imageUrls", column = "image_urls", javaType = List.class, typeHandler = dev.cheerfun.pixivic.common.handler.JsonTypeHandler.class),
            @Result(property = "tags", column = "tags", javaType = List.class, typeHandler = dev.cheerfun.pixivic.common.handler.JsonTypeHandler.class)
    })
    Illustration queryIllustrationByIllustId(String illustId);

    @Select("select * from illusts where artist_id = #{artistId} and sanity_level<=#{maxSanityLevel} limit #{currIndex} , #{pageSize}")
    @Results({
            @Result(property = "id", column = "illust_id"),
            @Result(property = "artistPreView", column = "artist", javaType = ArtistPreView.class, typeHandler = dev.cheerfun.pixivic.common.handler.JsonTypeHandler.class),
            @Result(property = "tools", column = "tools", javaType = List.class, typeHandler = dev.cheerfun.pixivic.common.handler.JsonTypeHandler.class),
            @Result(property = "tags", column = "tags", javaType = List.class, typeHandler = dev.cheerfun.pixivic.common.handler.JsonTypeHandler.class),
            @Result(property = "imageUrls", column = "image_urls", javaType = List.class, typeHandler = dev.cheerfun.pixivic.common.handler.JsonTypeHandler.class),
            @Result(property = "tags", column = "tags", javaType = List.class, typeHandler = dev.cheerfun.pixivic.common.handler.JsonTypeHandler.class)
    })
    List<Illustration> queryIllustrationsByArtistId(String artistId, int currIndex, int pageSize, int maxSanityLevel);

    @Select("select * from artists where artist_id =#{artistId}")
    @Results({
            @Result(property = "id", column = "artist_id"),
    })
    Artist queryArtistById(String artistId);
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
            @Result(property = "artistPreView", column = "artist", javaType = ArtistPreView.class, typeHandler = dev.cheerfun.pixivic.common.handler.JsonTypeHandler.class),
            @Result(property = "tools", column = "tools", javaType = List.class, typeHandler = dev.cheerfun.pixivic.common.handler.JsonTypeHandler.class),
            @Result(property = "tags", column = "tags", javaType = List.class, typeHandler = dev.cheerfun.pixivic.common.handler.JsonTypeHandler.class),
            @Result(property = "imageUrls", column = "image_urls", javaType = List.class, typeHandler = dev.cheerfun.pixivic.common.handler.JsonTypeHandler.class),
            @Result(property = "tags", column = "tags", javaType = List.class, typeHandler = dev.cheerfun.pixivic.common.handler.JsonTypeHandler.class)
    })
    List<Illustration> queryRandomIllustration();
}
