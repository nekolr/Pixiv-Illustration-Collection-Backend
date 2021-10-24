package dev.cheerfun.pixivic.biz.web.artist.secmapper;

import dev.cheerfun.pixivic.common.po.Artist;
import dev.cheerfun.pixivic.common.po.ArtistSummary;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ArtistBizMapper {
    @Select("select SQL_NO_CACHE * from artists where artist_id =#{artistId}")
    @Results({
            @Result(property = "id", column = "artist_id"),
            @Result(property = "birthDay", column = "birth_day"),
            @Result(property = "webPage", column = "webpage"),
            @Result(property = "twitterAccount", column = "twitter_account"),
            @Result(property = "twitterUrl", column = "twitter_url"),
            @Result(property = "totalFollowUsers", column = "total_follow_users"),
            @Result(property = "totalIllustBookmarksPublic", column = "total_illust_bookmarks_public"),
    })
    Artist queryArtistById(Integer artistId);

    @Select("select illust_id from illusts where artist_id = #{artistId} and type = #{type} order by create_date desc  limit #{currIndex} , #{pageSize}")
    List<Integer> queryIllustrationsByArtistId(Integer artistId, String type, int currIndex, int pageSize);

    @Select("select illust_id from illusts where artist_id = #{artistId} and type = #{type} order by create_date desc ")
    List<Integer> queryAllIllustrationIdByArtistId(Integer artistId, String type);

    @Select("select illust_sum,manga_sum from artist_summary where artist_id=#{artistId}")
    @Results({
            @Result(property = "illustSum", column = "illust_sum"),
            @Result(property = "mangaSum", column = "manga_sum"),
    })
    ArtistSummary querySummaryByArtistId(Integer artistId);
}
