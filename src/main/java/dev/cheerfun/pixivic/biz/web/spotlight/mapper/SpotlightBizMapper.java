package dev.cheerfun.pixivic.biz.web.spotlight.mapper;

import dev.cheerfun.pixivic.biz.web.rank.po.Rank;
import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.po.Spotlight;
import dev.cheerfun.pixivic.common.po.illust.ArtistPreView;
import dev.cheerfun.pixivic.common.util.json.JsonTypeHandler;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/09/12 14:27
 * @description RankMapper
 */
@Mapper
public interface SpotlightBizMapper {
    @Insert("insert into ranks(`mode`,`date`,`data`) values(#{mode}, #{date},#{data,typeHandler=dev.cheerfun.pixivic.common.util.json.JsonTypeHandler})")
    int insert(Rank rank);


    @Select("select * from spotlights limit #{currIndex} , #{pageSize}")
    @Results({
            @Result(property="id", column="spotlight_id"),
            @Result(property="pureTitle", column="pure_title"),
            @Result(property="publishDate", column="publish_date"),
            @Result(property="subcategoryLabel", column="subcategory_label"),
            @Result(property="articleUrl", column="article_url")
    })
    List<Spotlight> queryList(int pageSize, int currIndex);

    @Select("select * from spotlights where spotlight_id = #{spotlightId} limit 1")
    @Results({
            @Result(property="pureTitle", column="pure_title"),
            @Result(property="id", column="spotlight_id"),
            @Result(property="publishDate", column="publish_date"),
            @Result(property="subcategoryLabel", column="subcategory_label"),
            @Result(property="articleUrl", column="article_url")
    })
    Spotlight queryDetail(int spotlightId);

    @Select("SELECT * FROM illusts WHERE illust_id IN (SELECT illust_id from spotlight_illust_relation WHERE spotlight_id=#{spotlight_id})")
    @Results({
            @Result(property = "id", column = "illust_id"),
            @Result(property = "artistPreView", column = "artist", javaType = ArtistPreView.class, typeHandler = JsonTypeHandler.class),
            @Result(property = "tools", column = "tools", javaType = List.class, typeHandler = JsonTypeHandler.class),
            @Result(property = "tags", column = "tags", javaType = List.class, typeHandler = JsonTypeHandler.class),
            @Result(property = "imageUrls", column = "image_urls", javaType = List.class, typeHandler = JsonTypeHandler.class),
            @Result(property = "tags", column = "tags", javaType = List.class, typeHandler = JsonTypeHandler.class)
    })
    List<Illustration> queryIllustrations(int spotlightId);

}
