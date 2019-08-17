package dev.cheerfun.pixivic.web.user.mapper;

import dev.cheerfun.pixivic.web.user.model.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {
    @Select({
            "SELECT IFNULL((\n" +
                    "SELECT 1\n" +
                    "FROM USER\n" +
                    "WHERE email=#{email} OR username=#{username}\n" +
                    "LIMIT 1),0)",
    })
    int checkUserNameAndEmail(String username, String email);

    @Insert("insert into users (email, username,password,permission_level,is_ban) values (#{email}, #{username}, #{password}, #{permissionLevel}, #{isBan})")
    @Options(useGeneratedKeys = true, keyProperty = "userId", keyColumn = "user_id")
    void insertUser(User user);

    @Select({
            "SELECT *\n" +
                    "FROM USER\n" +
                    "WHERE username=#{username} AND PASSWORD=#{password}",
    })
    User getUser(String username, String password);

    @Select({
            "SELECT *\n" +
                    "FROM USER\n" +
                    "WHERE qq_access_token=#{qqAccessToken} ",
    })
    User getUser(String qqAccessToken);

    @Update("update users set name=#{qqAccessToken} where user_id=#{userId}")
    int setQqAccessToken(String qqAccessToken, String userId);

    @Update("update users set avatar=#{avatar} where user_id=#{userId}")
    int setAvatar(String avatar, String userId);

    @Update("update users set email=#{email} where user_id=#{userId}")
    int setEmail(String email, String userId);

    @Update("update users set password=#{password} where user_id=#{userId}")
    int setPassword(String password, String userId);
}
