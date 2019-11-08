package dev.cheerfun.pixivic.web.search.mapper;

import dev.cheerfun.pixivic.web.search.model.Response.PixivSearchSuggestion;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PixivSuggestionMapper {
    @Insert({
            "<script>",
            "insert IGNORE into artists values ",
            "<foreach collection='pixivSearchSuggestionList' item='pixivSearchSuggestion' index='index' separator=','>",
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
    int insert(String keyword, List<PixivSearchSuggestion> pixivSearchSuggestionList);
}
