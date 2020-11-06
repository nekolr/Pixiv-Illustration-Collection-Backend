package dev.cheerfun.pixivic.biz.web.app.mapper;

import dev.cheerfun.pixivic.biz.web.app.po.AppVersionInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/8/31 6:22 下午
 * @description AppMapper
 */

public interface AppMapper {
    @Select("select * from app_version_info order by release_date desc limit 1")
    @Results({
            @Result(column = "app_version_info_id", property = "id")
    })
    AppVersionInfo queryLatest();

    @Select("select * from app_version_info order by release_date desc limit #{currIndex},#{pageSize}")
    @Results({
            @Result(column = "app_version_info_id", property = "id")
    })
    List<AppVersionInfo> queryList(Integer currIndex, Integer pageSize);

    @Select("select count(*) from app_version_info")
    Integer queryCount();
}
