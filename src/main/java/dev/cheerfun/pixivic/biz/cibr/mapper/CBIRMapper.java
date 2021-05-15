package dev.cheerfun.pixivic.biz.cibr.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface CBIRMapper {

    @Insert("insert ignore into illust_features (illust_id,image_page,feature) values (#{illustId},#{imagePage},#{feature})")
    Integer insertFeature(Integer illustId, Integer imagePage, String feature);

    @Update("update image_feature_sync  set is_finish =1 where illust_id=#{illustId}")
    void updateImageSync(int illustId);

    @Insert("insert into image_vector_sync_to_milvus_check(illust_id,image_page,is_sync) values(#{illustId},#{imagePage},1)")
    void finishSyncToMilvus(Long illustId, Integer imagePage);

    @Select("select  is_sync  from image_vector_sync_to_milvus_check where illust_id=#{illustId} and image_page=#{imagePage}")
    Integer isSync(Long illustId, Integer imagePage);
}
