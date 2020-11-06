package dev.cheerfun.pixivic.common.util.translate.mapper;

import dev.cheerfun.pixivic.common.util.translate.domain.AzureApiKey;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/8/20 1:08 下午
 * @description TranslateMapper
 */

public interface TranslateMapper {
    @Select("select * from azure_tran_keys where use_flag = 1")
    @Results({
            @Result(column = "key_id", property = "id")
    })
    List<AzureApiKey> queryAll();

    @Update("update azure_tran_keys set use_flag = 1 where 1=1")
    void resetStatus();

    @Update("update azure_tran_keys set use_flag = 0 where key_id=#{id}")
    void ban(Integer id);

    @Update("update azure_tran_keys set use_flag = 2 where key_id=#{id}")
    void invalid(Integer id);
}
