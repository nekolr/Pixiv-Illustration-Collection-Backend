package dev.cheerfun.pixivic.biz.web.search.mapper;

import dev.cheerfun.pixivic.biz.web.search.dto.SearchSuggestionSyncDTO;
import dev.cheerfun.pixivic.common.po.illust.Tag;
import dev.cheerfun.pixivic.common.util.json.JsonTypeHandler;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PixivSuggestionMapper {
    @Insert({
            "<script>",
            "insert IGNORE into tag_suggestion (`tag`, `suggestion_tag`,`suggestion_tag_id`) values ",
            "<foreach collection='tags' item='tag' index='index' separator=','>",
            "(#{keyword}, #{tag,typeHandler=dev.cheerfun.pixivic.common.util.json.JsonTypeHandler}," +
                    "(select tag_id from tags where  name=ifnull(#{tag.name},'') and translated_name=ifnull(#{tag.translatedName},'')))",
            "</foreach>",
            "</script>"
    })
    int insert(String keyword, @Param("tags") List<Tag> tags);

    @Select("SELECT distinct suggestion_tag FROM tag_suggestion where suggestion_tag_id is null")
    @Results({
            @Result(property = "searchSuggestion", column = "suggestion_tag", javaType = Tag.class, typeHandler = JsonTypeHandler.class)
    })
    List<SearchSuggestionSyncDTO> queryByNoSuggestId();

    @Update("update tag_suggestion set suggestion_tag_id= #{tagId} where suggestion_tag=#{tag,typeHandler=dev.cheerfun.pixivic.common.util.json.JsonTypeHandler}")
    int updateSuggestionTagId(@Param("tag") Tag tag, @Param("tagId")Long tagId);
}
