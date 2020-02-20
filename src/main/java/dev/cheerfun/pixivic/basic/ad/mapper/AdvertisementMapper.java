package dev.cheerfun.pixivic.basic.ad.mapper;

import dev.cheerfun.pixivic.basic.ad.domain.AdvertisementInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AdvertisementMapper {
    List<AdvertisementInfo> queryAllEnableAdvertisementInfo();
}
