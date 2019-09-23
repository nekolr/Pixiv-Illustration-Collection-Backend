package dev.cheerfun.pixivic.web.user.mapper;

import dev.cheerfun.pixivic.common.model.Artist;
import dev.cheerfun.pixivic.common.model.Illustration;
import dev.cheerfun.pixivic.common.model.illust.ArtistPreView;
import dev.cheerfun.pixivic.common.model.illust.Tag;
import org.apache.ibatis.annotations.*;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface BusinessMapper {

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

    @Select("select * from illusts where artist_id = #{artistId} limit #{currIndex} , #{pageSize}")
    @Results({
            @Result(property = "id", column = "illust_id"),
            @Result(property = "artistPreView", column = "artist", javaType = ArtistPreView.class, typeHandler = dev.cheerfun.pixivic.common.handler.JsonTypeHandler.class),
            @Result(property = "tools", column = "tools", javaType = List.class, typeHandler = dev.cheerfun.pixivic.common.handler.JsonTypeHandler.class),
            @Result(property = "tags", column = "tags", javaType = List.class, typeHandler = dev.cheerfun.pixivic.common.handler.JsonTypeHandler.class),
            @Result(property = "imageUrls", column = "image_urls", javaType = List.class, typeHandler = dev.cheerfun.pixivic.common.handler.JsonTypeHandler.class),
            @Result(property = "tags", column = "tags", javaType = List.class, typeHandler = dev.cheerfun.pixivic.common.handler.JsonTypeHandler.class)
    })
    List<Illustration> queryIllustrationsByArtistId(String artistId, int currIndex, int pageSize);

    @Select("select tags from illusts where illust_id = #{illustId} ")
    @Results({
            @Result(property = "tags", column = "tags", javaType = List.class, typeHandler = dev.cheerfun.pixivic.common.handler.JsonTypeHandler.class)
    })
    List<Tag> queryIllustrationTagsByid(String illustId);

    @Update("update illusts set tags=#{tags,typeHandler=dev.cheerfun.pixivic.common.handler.JsonTypeHandler} where illust_id=#{illustId}")
    Illustration updateIllustrationTagsByid(String illustId, List<Tag> tags);

    @Select({"<script>",
            "select * from illusts where illust_id in (",
            "<foreach collection='illustIds' item='illustId' index='index' separator=',' close=')'>",
            "#{illustId}",
            "</foreach>",
            " limit #{currIndex} , #{pageSize}",
            "</script>"
    })
    @Results({
            @Result(property = "id", column = "illust_id"),
            @Result(property = "artistPreView", column = "artist", javaType = ArtistPreView.class, typeHandler = dev.cheerfun.pixivic.common.handler.JsonTypeHandler.class),
            @Result(property = "tools", column = "tools", javaType = List.class, typeHandler = dev.cheerfun.pixivic.common.handler.JsonTypeHandler.class),
            @Result(property = "tags", column = "tags", javaType = List.class, typeHandler = dev.cheerfun.pixivic.common.handler.JsonTypeHandler.class),
            @Result(property = "imageUrls", column = "image_urls", javaType = List.class, typeHandler = dev.cheerfun.pixivic.common.handler.JsonTypeHandler.class),
            @Result(property = "tags", column = "tags", javaType = List.class, typeHandler = dev.cheerfun.pixivic.common.handler.JsonTypeHandler.class)
    })
    List<Illustration> queryBookmarked(@Param("illustIds") List<Integer> illustIds, int currIndex, int pageSize);

    @Select({"<script>",
            "select * from artists where artist_id in ",
            "<foreach collection='artistIds' item='artistId' index='index' separator=',' close=')'>",
            "#{artistId}",
            "</foreach>",
            " limit #{currIndex} , #{pageSize}",
            "</script>"
    })
    List<Artist> queryFollowed(ArrayList<String> artistIds, int currIndex, int pageSize);

    @Update("update illusts set total_bookmarks=total_bookmarks+#{increment}  where illust_id=#{illustId}")
    int updateIllustBookmark(int illustId, int increment);

    @Update("update users set star=star+#{increment}  where user_id=#{userId}")
    int updateUserStar(int userId, int increment);
}
