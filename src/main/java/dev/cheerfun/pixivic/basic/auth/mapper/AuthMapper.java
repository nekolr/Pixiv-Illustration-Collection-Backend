package dev.cheerfun.pixivic.basic.auth.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Set;

//@Mapper
public interface AuthMapper {
    @Update("<script>" +
            "update users set last_active_time = now() where user_id in" +
            "<foreach collection='list' item='item' index='index'  separator=',' open='('  close=')'>" +
            "#{item}" +
            "</foreach>" +
            "</script>")
    Integer updateUserLastActiveTime(@Param("list") Set<Integer> userSet);
}
