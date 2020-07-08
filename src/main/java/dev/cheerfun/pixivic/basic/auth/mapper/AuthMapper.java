package dev.cheerfun.pixivic.basic.auth.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.Set;

@Mapper
public interface AuthMapper {
    @Update("<script>" +
            "update users set last_active_time = now() where user_id in (" +
            "<foreach collection='userSet' item='u' index='index'  separator=','>" +
            "(#{u})" +
            "</foreach>" +
            ")" +
            "</script>")
    Integer updateUserLastActiveTime(@Param("userSet") Set<Integer> userSet);
}
