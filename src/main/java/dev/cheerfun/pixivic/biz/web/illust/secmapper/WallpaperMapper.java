package dev.cheerfun.pixivic.biz.web.illust.secmapper;

import dev.cheerfun.pixivic.biz.wallpaper.po.WallpaperCategory;
import dev.cheerfun.pixivic.common.po.illust.Tag;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface WallpaperMapper {
    @Select("select tag_category_id,tag_category_name,tag_count from wallpaper_tag_category order by order_num")
    @Results({
            @Result(column = "tag_category_id", property = "id"),
            @Result(column = "tag_category_name", property = "name"),
            @Result(column = "tag_count", property = "tagCount")
    })
    List<WallpaperCategory> queryAllCate();

    @Select("select tag_id,tag_name,tag_translated_name from wallpapers_tag_category_tag_relation where  tag_category_id=#{categoryId} and tag_id > #{offset} order by tag_id limit #{pageSize}")
    @Results({
            @Result(column = "tag_id", property = "id"),
            @Result(column = "tag_name", property = "name"),
            @Result(column = "tag_translated_name", property = "translatedName")
    })
    List<Tag> queryTagListByCategory(Integer categoryId, Integer offset, Integer pageSize);

    @Select("select illust_id from wallpapers where tag_id =#{tagId} and wallpaper_type =#{type} and illust_id < #{offset} limit #{pageSize}")
    List<Integer> queryIllustIdByTag(Integer tagId, Integer type, Integer offset, Integer pageSize);

    @Select("select count(*) from wallpapers where tag_id =#{tagId} and wallpaper_type =#{type} ")
    Integer queryIllustCountByTag(Integer tagId, Integer type);
}
