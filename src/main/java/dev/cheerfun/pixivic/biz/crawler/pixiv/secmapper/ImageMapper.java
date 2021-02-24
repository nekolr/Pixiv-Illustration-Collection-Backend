package dev.cheerfun.pixivic.biz.crawler.pixiv.secmapper;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface ImageMapper {
    @Select("select illust_id from image_sync where is_finish=0 order by illust_id limit 1")
    Integer queryLatest();

    @Select("select illust_id from image_sync where is_finish=0 and illust_id >#{flag}    order by illust_id limit 10")
    List<Integer> queryIllustIdList(int flag);

    @Update("update image_sync  set is_finish =1 where illust_id=#{illustId}")
    void updateImageSync(int illustId);
}
