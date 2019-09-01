package dev.cheerfun.pixivic.web.search.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CandidatesMapper {
    @Insert({
            "<script>",
            "insert IGNORE into artists values ",
            "<foreach collection='artists' item='artist' index='index' separator=','>",
            "(#{artist.id}, #{artist.name}," +
                    "#{artist.account}, #{artist.avatar}," +
                    "#{artist.comment}, #{artist.gender}," +
                    "#{artist.birthDay #{artist.region}," +
                    "#{artist.webPage}, #{artist.twitterAccount}," +
                    "#{artist.twitterUrl}, #{artist.totalFollowUsers}," +
                    "#{artist.totalIllustBookmarksPublic})",
            "</foreach>",
            "</script>"
    })
    int insert(String tagName, List<String> keywordList);

    @Select({
            "<script>",
            "SELECT temp.artist_id FROM (",
            "<foreach collection='artistIds' item='artistId' index='index' separator='UNION ALL'>",
            "(SELECT #{artistId} AS artist_id )",
            "</foreach>",
            ") as temp WHERE temp.artist_id  NOT IN (SELECT artists.artist_id FROM artists)",
            "</script>"
    })
    List<Integer> queryArtistsNotInDb(String tagName);
}
