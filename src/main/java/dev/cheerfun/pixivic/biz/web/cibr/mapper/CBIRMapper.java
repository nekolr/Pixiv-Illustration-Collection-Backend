package dev.cheerfun.pixivic.biz.web.cibr.mapper;

import org.apache.ibatis.annotations.Insert;

public interface CBIRMapper {

    @Insert("insert ignore into illust_features (illust_id,image_page,feature) values (#{illustId},#{imagePage},#{feature})")
    Integer insertFeature(Integer illustId, Integer imagePage, String feature);

}
