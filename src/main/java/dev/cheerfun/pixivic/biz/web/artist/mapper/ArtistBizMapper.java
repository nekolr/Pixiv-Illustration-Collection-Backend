package dev.cheerfun.pixivic.biz.web.artist.mapper;

import dev.cheerfun.pixivic.biz.web.user.dto.UserListDTO;
import dev.cheerfun.pixivic.common.po.Artist;
import dev.cheerfun.pixivic.common.po.ArtistSummary;
import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.po.illust.ArtistPreView;
import dev.cheerfun.pixivic.common.util.json.JsonTypeHandler;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ArtistBizMapper {
    @Select("select * from artists where artist_id =#{artistId}")
    @Results({
            @Result(property = "id", column = "artist_id"),
    })
    Artist queryArtistById(Integer artistId);

    @Select("select user_id,username,create_date from user_artist_followed where artist_id=#{artistId} order by id desc  limit #{currIndex} , #{pageSize}")
    @Results({
            @Result(property = "illustId", column = "illust_id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "createDate", column = "create_Date", typeHandler = org.apache.ibatis.type.LocalDateTimeTypeHandler.class)
    })
    List<UserListDTO> queryUserListFollowedArtist(Integer artistId, int currIndex, Integer pageSize);

    @Select("select illust_id from illusts where artist_id = #{artistId} and type = #{type} order by create_date desc  limit #{currIndex} , #{pageSize}")
    List<Integer> queryIllustrationsByArtistId(Integer artistId, String type, int currIndex, int pageSize);

    @Select("select illust_sum,manga_sum from artist_summary where artist_id=#{artistId}")
    @Results({
            @Result(property = "illustSum", column = "illust_sum"),
            @Result(property = "mangaSum", column = "manga_sum"),
    })
    ArtistSummary querySummaryByArtistId(Integer artistId);
}
