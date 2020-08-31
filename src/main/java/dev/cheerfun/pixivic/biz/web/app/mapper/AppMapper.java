package dev.cheerfun.pixivic.biz.web.app.mapper;

import dev.cheerfun.pixivic.biz.web.app.po.AppVersionInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/8/31 6:22 下午
 * @description AppMapper
 */
@Mapper
public interface AppMapper {
    @Select("select * from app_version_info order by release_date desc limit 1")
    AppVersionInfo queryLatest();

    @Select("select * from app_version_info order by release_date desc limit #{currIndex},#{pageSize}")
    List<AppVersionInfo> queryList(Integer currIndex, Integer pageSize);
}
