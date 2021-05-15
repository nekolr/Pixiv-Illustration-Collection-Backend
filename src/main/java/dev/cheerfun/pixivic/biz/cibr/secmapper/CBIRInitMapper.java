package dev.cheerfun.pixivic.biz.cibr.secmapper;

import dev.cheerfun.pixivic.biz.cibr.po.IllustFeature;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface CBIRInitMapper {
    @Select("select illust_id from image_feature_sync where is_finish=0 order by illust_id limit 1")
    Integer queryLatest();

    @Select("select illust_id from image_feature_sync where is_finish=0 and illust_id >#{flag}    order by illust_id limit 50")
    List<Integer> queryIllustIdList(int flag);

    @Update("update image_feature_sync  set is_finish =1 where illust_id=#{illustId}")
    void updateImageSync(int illustId);

    @Select("select illust_id,feature,image_page from illust_features where illust_id>#{flag} order by illust_id,image_page limit 5000")
    @Results({
            @Result(property = "illustId", column = "illust_id"),
            @Result(property = "imagePage", column = "image_page"),
            @Result(property = "feature", column = "feature"/*, javaType = List.class, typeHandler = JsonTypeHandler.class*/)
    })
    List<IllustFeature> queryVectorList(Long flag);

    @Insert("insert into image_vector_sync_to_milvus_check(illust_id,image_page,is_sync) values(#{illustId},#{imagePage},1)")
    void finishSyncToMilvus(Long illustId, Integer imagePage);

    @Select("select  is_sync  from image_vector_sync_to_milvus_check where illust_id=#{illustId} and image_page=#{imagePage}")
    Integer isSync(Long illustId, Integer imagePage);

}
