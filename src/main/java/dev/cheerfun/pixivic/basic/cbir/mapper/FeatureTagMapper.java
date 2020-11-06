package dev.cheerfun.pixivic.basic.cbir.mapper;

import dev.cheerfun.pixivic.basic.cbir.domain.FeatureTag;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface FeatureTagMapper {
    @Select("select * from feature_tags where tag_type in (0,1) and extend_content is null")
    @Results({
            @Result(property = "id", column = "feature_tag_id"),
            @Result(property = "content", column = "tag_content"),
            @Result(property = "type", column = "tag_type"),
            @Result(property = "pixivTags", column = "pixiv_tags"),
            @Result(property = "desc", column = "tag_desc"),
            @Result(property = "CNDesc", column = "tag_cn_desc"),
            @Result(property = "extendContent", column = "extend_content")
    })
    List<FeatureTag> queryAll();

    @Update("update feature_tags set pixiv_tags=#{pixivTags},tag_desc=#{desc},tag_cn_desc=#{CNDesc},extend_content=#{extendContent} where feature_tag_id=#{id}")
    Integer updateById(FeatureTag featureTag);
}
