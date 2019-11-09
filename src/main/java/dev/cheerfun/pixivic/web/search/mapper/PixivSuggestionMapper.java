package dev.cheerfun.pixivic.web.search.mapper;

import dev.cheerfun.pixivic.web.search.model.Response.PixivSearchSuggestion;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PixivSuggestionMapper {
    @Insert({
            "<script>",
            "insert IGNORE into tag_suggestion (`tag`, `suggestion_tag`,`suggestion_tag_id`) values ",
            "<foreach collection='pixivSearchSuggestionList' item='pixivSearchSuggestion' index='index' separator=','>",
            "(#{keyword}, #{pixivSearchSuggestion,typeHandler=dev.cheerfun.pixivic.common.handler.JsonTypeHandler}," +
                    "(select tag_id from tags where  name=#{pixivSearchSuggestion.tag} and translated_name=#{pixivSearchSuggestion.tag_translation}))",
            "</foreach>",
            "</script>"
    })
    int insert(String keyword, @Param("pixivSearchSuggestionList") List<PixivSearchSuggestion> pixivSearchSuggestionList);
}
