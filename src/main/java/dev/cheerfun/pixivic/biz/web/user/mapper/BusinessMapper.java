package dev.cheerfun.pixivic.biz.web.user.mapper;

import dev.cheerfun.pixivic.common.model.Artist;
import dev.cheerfun.pixivic.common.model.Illustration;
import dev.cheerfun.pixivic.common.model.illust.ArtistPreView;
import dev.cheerfun.pixivic.common.model.illust.Tag;
import dev.cheerfun.pixivic.common.util.json.JsonTypeHandler;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BusinessMapper {
    @Select("select tags from illusts where illust_id = #{illustId} ")
    @Results({
            @Result(property = "tags", column = "tags", javaType = List.class, typeHandler = JsonTypeHandler.class)
    })
    List<Tag> queryIllustrationTagsById(String illustId);

    @Update("update illusts set tags=#{tags,typeHandler=dev.cheerfun.pixivic.common.util.json.JsonTypeHandler} where illust_id=#{illustId}")
    int updateIllustrationTagsById(String illustId, List<Tag> tags);

    @Select({"<script>",
            "select * from illusts where illust_id in (",
            "<foreach collection='illustIds' item='illustId' index='index' separator=',' close=')'>",
            "#{illustId}",
            "</foreach>",
            "and type = #{type} limit #{currIndex} , #{pageSize}",
            "</script>"
    })
    @Results({
            @Result(property = "id", column = "illust_id"),
            @Result(property = "artistPreView", column = "artist", javaType = ArtistPreView.class, typeHandler = JsonTypeHandler.class),
            @Result(property = "tools", column = "tools", javaType = List.class, typeHandler = JsonTypeHandler.class),
            @Result(property = "tags", column = "tags", javaType = List.class, typeHandler = JsonTypeHandler.class),
            @Result(property = "imageUrls", column = "image_urls", javaType = List.class, typeHandler = JsonTypeHandler.class),
            @Result(property = "tags", column = "tags", javaType = List.class, typeHandler = JsonTypeHandler.class)
    })
    List<Illustration> queryBookmarked(@Param("illustIds") List<Integer> illustIds, String type, int currIndex, int pageSize);

    @Select({"<script>",
            "select * from artists where artist_id in (",
            "<foreach collection='artistIds' item='artistId' index='index' separator=',' close=')'>",
            "#{artistId}",
            "</foreach>",
            " limit #{currIndex} , #{pageSize}",
            "</script>"
    })
    @Results({
            @Result(property = "id", column = "artist_id"),
    })
    List<Artist> queryFollowed(List<Integer> artistIds, int currIndex, int pageSize);

    @Update("update illusts set total_bookmarks=total_bookmarks+#{increment}  where illust_id=#{illustId}")
    int updateIllustBookmark(int illustId, int increment);

    @Update("update users set star=star+#{increment}  where user_id=#{userId}")
    int updateUserStar(int userId, int increment);

    @Select({"<script>",
            "select * from illusts where artist_id in (",
            "<foreach collection='artistIds' item='artistId' index='index' separator=',' close=')'>",
            "#{artistId}",
            "</foreach>",
            "and type = #{type} order by create_date desc  limit #{currIndex} , #{pageSize}",
            "</script>"})
    @Results({
            @Result(property = "id", column = "illust_id"),
            @Result(property = "artistPreView", column = "artist", javaType = ArtistPreView.class, typeHandler = JsonTypeHandler.class),
            @Result(property = "tools", column = "tools", javaType = List.class, typeHandler = JsonTypeHandler.class),
            @Result(property = "tags", column = "tags", javaType = List.class, typeHandler = JsonTypeHandler.class),
            @Result(property = "imageUrls", column = "image_urls", javaType = List.class, typeHandler = JsonTypeHandler.class),
            @Result(property = "tags", column = "tags", javaType = List.class, typeHandler = JsonTypeHandler.class)
    })
    List<Illustration> queryFollowedLatest(List<Integer> artists, String type, int currIndex, int pageSize);
}
