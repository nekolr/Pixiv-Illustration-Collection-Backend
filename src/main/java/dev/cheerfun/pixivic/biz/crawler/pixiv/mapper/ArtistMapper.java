package dev.cheerfun.pixivic.biz.crawler.pixiv.mapper;

import dev.cheerfun.pixivic.common.po.Artist;
import org.apache.ibatis.annotations.*;

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
            "insert IGNORE into artists(artist_id,name,account,avatar,comment,gender,birth_day,region,webpage,twitter_account,twitter_url,total_follow_users,total_illust_bookmarks_public) values ",
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

    @Update("replace into artist_summary (artist_id, illust_sum, manga_sum) select #{artistId}, sum(IF(type='illust',1,0)) ,sum(IF(type='manga',1,0)) from illusts where artist_id=#{artistId}")
    void updateArtistSummary(Integer artistId);
}
