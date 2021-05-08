package dev.cheerfun.pixivic.biz.web.cibr.mapper;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface CBIRInitMapper {
    @Select("select illust_id from image_feature_sync where is_finish=0 order by illust_id limit 1")
    Integer queryLatest();

    @Select("select illust_id from image_feature_sync where is_finish=0 and illust_id >#{flag}    order by illust_id limit 50")
    List<Integer> queryIllustIdList(int flag);

    @Update("update image_feature_sync  set is_finish =1 where illust_id=#{illustId}")
    void updateImageSync(int illustId);

}
