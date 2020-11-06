package dev.cheerfun.pixivic.biz.web.spotlight.secmapper;

import dev.cheerfun.pixivic.biz.web.illust.po.Rank;
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

public interface SpotlightBizMapper {
    @Insert("insert into ranks(`mode`,`date`,`data`) values(#{mode}, #{date},#{data,typeHandler=dev.cheerfun.pixivic.common.util.json.JsonTypeHandler})")
    int insert(Rank rank);

    @Select("select spotlight_id from spotlights order by publish_date desc limit #{currIndex} , #{pageSize}")
    List<Integer> queryList(int pageSize, int currIndex);

    @Select("select * from spotlights where spotlight_id = #{spotlightId}")
    @Results({
            @Result(property = "pureTitle", column = "pure_title"),
            @Result(property = "id", column = "spotlight_id"),
            @Result(property = "publishDate", column = "publish_date", typeHandler = org.apache.ibatis.type.LocalDateTypeHandler.class),
            @Result(property = "subcategoryLabel", column = "subcategory_label"),
            @Result(property = "articleUrl", column = "article_url")
    })
    Spotlight queryDetail(int spotlightId);

    @Select("SELECT illust_id from spotlight_illust_relation WHERE spotlight_id=#{spotlight_id}")
    List<Integer> queryIllustrations(int spotlightId);

}
