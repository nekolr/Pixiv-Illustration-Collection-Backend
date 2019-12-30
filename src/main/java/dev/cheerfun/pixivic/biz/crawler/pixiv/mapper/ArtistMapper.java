package dev.cheerfun.pixivic.biz.crawler.pixiv.mapper;

import dev.cheerfun.pixivic.common.po.Artist;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/09/12 14:27
 * @description ArtistMapper
 */

@Mapper
public interface ArtistMapper {
    @Insert({
            "<script>",
            "insert IGNORE into artists values ",
            "<foreach collection='artists' item='artist' index='index' separator=','>",
            "(#{artist.id}, #{artist.name}," +
                    "#{artist.account}, #{artist.avatar}," +
                    "#{artist.comment}, #{artist.gender}," +
                    "#{artist.birthDay}, #{artist.region}," +
                    "#{artist.webPage}, #{artist.twitterAccount}," +
                    "#{artist.twitterUrl}, #{artist.totalFollowUsers}," +
                    "#{artist.totalIllustBookmarksPublic})",
            "</foreach>",
            "</script>"
    })
    int insert(@Param("artists") List<Artist> artists);

    @Select({
            "<script>",
            "SELECT temp.artist_id FROM (",
            "<foreach collection='artistIds' item='artistId' index='index' separator='UNION ALL'>",
            "(SELECT #{artistId} AS artist_id )",
            "</foreach>",
            ") as temp WHERE temp.artist_id  NOT IN (SELECT artists.artist_id FROM artists)",
            "</script>"
    })
    List<Integer> queryArtistsNotInDb(@Param("artistIds") List<Integer> artists);
}
