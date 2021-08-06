package dev.cheerfun.pixivic.biz.recommend.secmapper;

import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.po.illust.ArtistPreView;
import dev.cheerfun.pixivic.common.util.json.JsonTypeHandler;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

public interface RecommendInitMapper {

    @Select("select * from illusts where  illust_id>#{index} and type='illust' and total_bookmarks>200 and sanity_level<7 and x_restrict=0 order by illust_id limit 10000")
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
    List<Illustration> queryIllustToInertItem(Integer index);

    @Insert("insert ignore into items (item_id,time_stamp,labels,comment) values(#{id},#{createDate},#{collect,typeHandler=dev.cheerfun.pixivic.common.util.json.JsonTypeHandler},'')")
    void insertIterm(Integer id, List<String> collect, Date createDate);
}
