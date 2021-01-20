package dev.cheerfun.pixivic.basic.review.secmapper;

import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.po.illust.ArtistPreView;
import dev.cheerfun.pixivic.common.util.json.JsonTypeHandler;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface IllustReviewMapper {

    @Select("select  * from illusts where illust_id > #{illustId} order by illust_id limit 100000")
    @Results({
            @Result(property = "id", column = "illust_id"),
            @Result(property = "artistPreView", column = "artist", javaType = ArtistPreView.class, typeHandler = JsonTypeHandler.class),
            @Result(property = "tools", column = "tools", javaType = List.class, typeHandler = JsonTypeHandler.class),
            @Result(property = "tags", column = "tags", javaType = List.class, typeHandler = JsonTypeHandler.class),
            @Result(property = "imageUrls", column = "image_urls", javaType = List.class, typeHandler = JsonTypeHandler.class),
            @Result(property = "tags", column = "tags", javaType = List.class, typeHandler = JsonTypeHandler.class),
            @Result(property = "artistId", column = "artist_id"),
            @Result(property = "pageCount", column = "page_count"),
            @Result(property = "sanityLevel", column = "sanity_level"),
            @Result(property = "totalBookmarks", column = "total_bookmarks"),
            @Result(property = "totalView", column = "total_view"),
            @Result(property = "xRestrict", column = "x_restrict"),
            @Result(property = "createDate", column = "create_date"),
    })
    List<Illustration> queryIllustrationByIllustId(Integer illustId);
@Insert("insert ignore into block_review_illusts(illust_id) values(#{e})")
    void insertIntoReviewBlockTable(Integer e);
}
