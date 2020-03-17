package dev.cheerfun.pixivic.biz.web.illust.mapper;

import dev.cheerfun.pixivic.biz.web.illust.po.IllustRelated;
import dev.cheerfun.pixivic.biz.web.user.dto.UserListDTO;
import dev.cheerfun.pixivic.common.po.Artist;
import dev.cheerfun.pixivic.common.po.ArtistSummary;
import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.po.illust.ArtistPreView;
import dev.cheerfun.pixivic.common.util.json.JsonTypeHandler;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface IllustrationBizMapper {
    @Select("select * from illusts where illust_id = #{illustId}")
    @Results({
            @Result(property = "id", column = "illust_id"),
            @Result(property = "artistPreView", column = "artist", javaType = ArtistPreView.class, typeHandler = JsonTypeHandler.class),
            @Result(property = "tools", column = "tools", javaType = List.class, typeHandler = JsonTypeHandler.class),
            @Result(property = "tags", column = "tags", javaType = List.class, typeHandler = JsonTypeHandler.class),
            @Result(property = "imageUrls", column = "image_urls", javaType = List.class, typeHandler = JsonTypeHandler.class),
            @Result(property = "tags", column = "tags", javaType = List.class, typeHandler = JsonTypeHandler.class)
    })
    Illustration queryIllustrationByIllustId(Integer illustId);

    @Select("select * from illusts where artist_id = #{artistId} and type = #{type} order by create_date desc  limit #{currIndex} , #{pageSize}")
    @Results({
            @Result(property = "id", column = "illust_id"),
            @Result(property = "artistPreView", column = "artist", javaType = ArtistPreView.class, typeHandler = JsonTypeHandler.class),
            @Result(property = "tools", column = "tools", javaType = List.class, typeHandler = JsonTypeHandler.class),
            @Result(property = "tags", column = "tags", javaType = List.class, typeHandler = JsonTypeHandler.class),
            @Result(property = "imageUrls", column = "image_urls", javaType = List.class, typeHandler = JsonTypeHandler.class),
            @Result(property = "tags", column = "tags", javaType = List.class, typeHandler = JsonTypeHandler.class)
    })
    List<Illustration> queryIllustrationsByArtistId(Integer artistId, String type, int currIndex, int pageSize);

    @Select("select * from artists where artist_id =#{artistId}")
    @Results({
            @Result(property = "id", column = "artist_id"),
    })
    Artist queryArtistById(Integer artistId);

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

    @Select("select illust_sum,manga_sum from artist_summary where artist_id=#{artistId}")
    @Results({
            @Result(property = "illustSum", column = "illust_sum"),
            @Result(property = "mangaSum", column = "manga_sum"),
    })
    ArtistSummary querySummaryByArtistId(Integer artistId);

    @Insert({
            "<script>",
            "replace into illust_related (`illust_id`, `related_illust_id`, `order_num`) values ",
            "<foreach collection='illustRelatedList' item='illustRelated' index='index' separator=','>",
            "(#{illustRelated.illustId}, #{illustRelated.relatedIllustId}, #{illustRelated.orderNum})",
            "</foreach>",
            "</script>"
    })
    int insertIllustRelated(@Param("illustRelatedList") List<IllustRelated> illustRelatedList);

    @Select({
            "<script>",
            "select *from illusts where illust_id in( ",
            "<foreach collection='illustIdList' item='illustId' index='index' separator=','>",
            "#{illustId}",
            "</foreach>",
            ") order by illust_id desc",
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
    List<Illustration> queryIllustrationByIllustIdList(@Param("illustIdList") List<Integer> illustIdList);

    @Select("select user_id,username,create_date from user_illust_bookmarked where illust_id=#{illustId} order by id desc  limit #{currIndex} , #{pageSize}")
    @Results({
            @Result(property = "illustId", column = "illust_id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "createDate", column = "create_Date", typeHandler = org.apache.ibatis.type.LocalDateTimeTypeHandler.class)
    })
    List<UserListDTO> queryUserListBookmarkedIllust(Integer illustId, int currIndex, int pageSize);

    @Select("select user_id,username,create_date from user_artist_followed where artist_id=#{artistId} order by id desc  limit #{currIndex} , #{pageSize}")
    @Results({
            @Result(property = "illustId", column = "illust_id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "createDate", column = "create_Date", typeHandler = org.apache.ibatis.type.LocalDateTimeTypeHandler.class)
    })
    List<UserListDTO> queryUserListFollowedArtist(Integer artistId, int currIndex, Integer pageSize);
}
