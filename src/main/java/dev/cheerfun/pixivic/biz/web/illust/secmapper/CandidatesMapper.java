package dev.cheerfun.pixivic.biz.web.illust.secmapper;

import org.apache.ibatis.annotations.Insert;

import java.util.List;

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

}
