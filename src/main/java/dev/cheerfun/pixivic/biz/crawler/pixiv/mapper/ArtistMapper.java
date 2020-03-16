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

    @Update("replace into artist_summary (artist_id, illust_sum, manga_sum)\n" +
            "select t.artist_id, IFNULL(t.illust_sum, 0), IFNULL(artist_summary.manga_sum, 0)\n" +
            "from (\n" +
            "         select artist_id, count(illust_id) illust_sum\n" +
            "         from illusts\n" +
            "         where type = 'illust' and artist_id=#{artistId}\n" +
            "         group by artist_id) t\n" +
            "         left join artist_summary on t.artist_id = artist_summary.artist_id;" +
            "replace into artist_summary (artist_id, manga_sum, illust_sum)\n" +
            "select t.artist_id, IFNULL(t.manga_sum, 0), IFNULL(artist_summary.illust_sum, 0)\n" +
            "from (\n" +
            "         select artist_id, count(illust_id) manga_sum\n" +
            "         from illusts\n" +
            "         where type = 'manga' and artist_id=#{artistId}\n" +
            "         group by artist_id) t\n" +
            "         left join artist_summary on t.artist_id = artist_summary.artist_id;")
    void updateArtistSummary(Integer artistId);
}
