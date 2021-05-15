package dev.cheerfun.pixivic.biz.cibr.mapper;

import dev.cheerfun.pixivic.biz.cibr.po.FeatureTag;
import dev.cheerfun.pixivic.common.util.json.JsonTypeHandler;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface DeepDanbooruMapper {
    @Select("select * from feature_tags where feature_tag_id=#{index}")
    @Results({
            @Result(property = "id", column = "feature_tag_id"),
            @Result(property = "content", column = "tag_content"),
            @Result(property = "type", column = "tag_type"),
            @Result(property = "pixivTags", column = "pixiv_tags", javaType = List.class, typeHandler = JsonTypeHandler.class),
            @Result(property = "desc", column = "tag_desc"),
            @Result(property = "CNDesc", column = "tag_cn_desc"),
            @Result(property = "extendContent", column = "extend_content")
    })
    FeatureTag queryTagListByIndex(Integer index);
}
