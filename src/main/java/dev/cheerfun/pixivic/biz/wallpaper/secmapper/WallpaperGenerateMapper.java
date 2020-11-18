package dev.cheerfun.pixivic.biz.wallpaper.secmapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

public interface WallpaperGenerateMapper {
    @Select("select illust_id from illusts where update_time between #{start,typeHandler=org.apache.ibatis.type.LocalDateTimeTypeHandler} and #{end ,typeHandler=org.apache.ibatis.type.LocalDateTimeTypeHandler } and total_bookmarks >=200 and sanity_level <7")
    List<Integer> queryIllustIdFilterForWallpaper(LocalDateTime start, LocalDateTime end);

    @Insert("insert ignore into wallpapers (tag_id,wallpaper_type,illust_id) values(#{tagId},#{wallpaperType},#{illustId})")
    void insertWallpaper(Long tagId, Integer wallpaperType, Integer illustId);

    @Select("select distinct illust_id from wallpapers where wallpaper_type=#{type}")
    List<Integer> queryAllWallpaperByType(int type);

    @Insert("insert into random_mobile_illust (random_index,illust_id) values (#{index},#{illustId})")
    void insertToRandomIndexTable(int type, int index, Integer illustId);
}
