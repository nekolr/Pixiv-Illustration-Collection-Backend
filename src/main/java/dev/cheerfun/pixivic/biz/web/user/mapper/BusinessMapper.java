package dev.cheerfun.pixivic.biz.web.user.mapper;

import dev.cheerfun.pixivic.common.po.Artist;
import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.po.illust.ArtistPreView;
import dev.cheerfun.pixivic.common.po.illust.Tag;
import dev.cheerfun.pixivic.common.util.json.JsonTypeHandler;
import org.apache.ibatis.annotations.*;
import org.springframework.cache.annotation.Cacheable;

import java.time.LocalDateTime;
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

/*
    @Select({"<script>",
            "select * from illusts where illust_id in (",
            "<foreach collection='illustIds' item='illustId' index='index' separator=',' close=')'>",
            "#{illustId}",
            "</foreach>",
            " order by field(illust_id,",
            "<foreach collection='illustIds' item='illustId' index='index' separator=',' close=')'>",
            "#{illustId}",
            "</foreach>",
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
    @Cacheable(value = "illust")
    List<Illustration> queryBookmarked(@Param("illustIds") List<Integer> illustIds, String type, int currIndex, int pageSize);
*/

    /*@Select({"<script>",
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
    List<Artist> queryFollowed(List<Integer> artistIds, int currIndex, int pageSize);*/

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

    @Insert("insert into user_artist_followed (user_id, artist_id,create_date) values (#{userId}, #{artistId}, #{now,typeHandler=org.apache.ibatis.type.LocalDateTimeTypeHandler})")
    int follow(int userId, int artistId, LocalDateTime now);

    @Delete("delete from user_artist_followed where user_id=#{userId} and artist_id = #{artistId}")
    int cancelFollow(int userId, int artistId);

    @Select("select a.* from (select artist_id from user_artist_followed where user_id = #{userId}  limit #{currIndex} , #{pageSize}) u left join artists a on u.artist_id = a.artist_id")
    @Results({
            @Result(property = "id", column = "artist_id"),
    })
    List<Artist> queryFollowed(int userId, int currIndex, int pageSize);

    @Select("select  count(id) from user_artist_followed where user_id = #{userId} and artist_id=#{artistId}")
    int queryIsFollowed(int userId, int artistId);

    @Insert("insert into user_illust_bookmarked (user_id, illust_id,create_date) values (#{userId}, #{illustId}, #{now,typeHandler=org.apache.ibatis.type.LocalDateTimeTypeHandler})")
    int bookmark(int userId, int illustId, LocalDateTime now);

    @Delete("delete from user_illust_bookmarked where id=#{relationId} ")
    int cancelBookmarkByid(int relationId);

    @Delete("delete from user_illust_bookmarked where user_id=#{userId} and illust_id=#{illustId} ")
    int cancelBookmark(int userId, int illustId);

    @Select("select i.* from (select illust_id from user_illust_bookmarked where user_id=#{userId}  limit #{currIndex} , #{pageSize}) u left join illusts i on  u.illust_id=i.illust_id where type=#{type}")
    @Results({
            @Result(property = "id", column = "illust_id"),
            @Result(property = "artistPreView", column = "artist", javaType = ArtistPreView.class, typeHandler = JsonTypeHandler.class),
            @Result(property = "tools", column = "tools", javaType = List.class, typeHandler = JsonTypeHandler.class),
            @Result(property = "tags", column = "tags", javaType = List.class, typeHandler = JsonTypeHandler.class),
            @Result(property = "imageUrls", column = "image_urls", javaType = List.class, typeHandler = JsonTypeHandler.class),
            @Result(property = "tags", column = "tags", javaType = List.class, typeHandler = JsonTypeHandler.class)
    })
    List<Illustration> queryBookmarked(int userId, String type, int currIndex, int pageSize);
    int queryIsBookmarked();
}
