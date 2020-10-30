package dev.cheerfun.pixivic.biz.web.user.mapper;

import dev.cheerfun.pixivic.biz.credit.po.CreditHistory;
import dev.cheerfun.pixivic.biz.web.common.po.User;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface CommonMapper {
    @Select({
            "SELECT IFNULL((\n" +
                    "SELECT 1\n" +
                    "FROM users\n" +
                    "WHERE username=#{username}\n" +
                    "LIMIT 1),0)",
    })
    int checkUserName(String username);

    @Select({
            "SELECT IFNULL((\n" +
                    "SELECT 1\n" +
                    "FROM users\n" +
                    "WHERE email=#{email} \n" +
                    "LIMIT 1),0)",
    })
    int checkUserEmail(String email);

    @Insert("insert into users (email, username,password,permission_level,is_ban,star,create_date) values (#{email}, #{username}, #{password}, #{permissionLevel}, #{isBan},#{star},#{createDate,typeHandler=org.apache.ibatis.type.LocalDateTimeTypeHandler})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "user_id")
    Integer insertUser(User user);

    @Select({
            " SELECT * FROM (SELECT * FROM users WHERE username= #{username} OR email=#{username})  temp where temp.PASSWORD=#{password}",
    })
    @Results({
            @Result(property = "id", column = "user_id"),
            @Result(property = "isBan", column = "is_ban"),
            @Result(property = "permissionLevel", column = "permission_level"),
            @Result(property = "pixivAccount", column = "pixiv_account"),
            @Result(property = "pixivPassword", column = "pixiv_password"),
            @Result(property = "qqOpenId", column = "qq_open_id"),
            @Result(property = "isCheckEmail", column = "is_check_email"),
            @Result(property = "createDate", column = "create_date", typeHandler = org.apache.ibatis.type.LocalDateTimeTypeHandler.class),
            @Result(property = "updateDate", column = "update_date", typeHandler = org.apache.ibatis.type.LocalDateTimeTypeHandler.class),
            @Result(property = "permissionLevelExpireDate", column = "permission_level_expire_date", typeHandler = org.apache.ibatis.type.LocalDateTimeTypeHandler.class)
    })
    User queryUserByusernameAndPassword(String username, String password);

    @Select({
            "SELECT * FROM users WHERE user_id= #{userId} ",
    })
    @Results({
            @Result(property = "id", column = "user_id"),
            @Result(property = "isBan", column = "is_ban"),
            @Result(property = "permissionLevel", column = "permission_level"),
            @Result(property = "pixivAccount", column = "pixiv_account"),
            @Result(property = "pixivPassword", column = "pixiv_password"),
            @Result(property = "qqOpenId", column = "qq_open_id"),
            @Result(property = "isCheckEmail", column = "is_check_email"),
            @Result(property = "createDate", column = "create_date", typeHandler = org.apache.ibatis.type.LocalDateTimeTypeHandler.class),
            @Result(property = "updateDate", column = "update_date", typeHandler = org.apache.ibatis.type.LocalDateTimeTypeHandler.class),
            @Result(property = "permissionLevelExpireDate", column = "permission_level_expire_date", typeHandler = org.apache.ibatis.type.LocalDateTimeTypeHandler.class)

    })
    User queryUserByUserId(int userId);

    @Select("SELECT * FROM users WHERE qq_open_id=#{qqOpenId}\n")
    @Results({
            @Result(property = "id", column = "user_id"),
            @Result(property = "isBan", column = "is_ban"),
            @Result(property = "permissionLevel", column = "permission_level"),
            @Result(property = "pixivAccount", column = "pixiv_account"),
            @Result(property = "pixivPassword", column = "pixiv_password"),
            @Result(property = "qqOpenId", column = "qq_open_id"),
            @Result(property = "isCheckEmail", column = "is_check_email"),
            @Result(property = "createDate", column = "create_date", typeHandler = org.apache.ibatis.type.LocalDateTimeTypeHandler.class),
            @Result(property = "updateDate", column = "update_date", typeHandler = org.apache.ibatis.type.LocalDateTimeTypeHandler.class),
            @Result(property = "permissionLevelExpireDate", column = "permission_level_expire_date", typeHandler = org.apache.ibatis.type.LocalDateTimeTypeHandler.class)
    })
    User getUserByQQOpenId(String qqOpenId);

    @Update("update users set qq_open_id=#{qqOpenId} where user_id=#{userId}")
    Integer setQQOpenId(String qqOpenId, int userId);

    @Update("update users set avatar=#{avatar} where user_id=#{userId}")
    Integer setAvatar(String avatar, int userId);

    @Update("update users set email=#{email} , is_check_email=1,permission_level= if(permission_level>1,permission_level,2) where user_id=#{userId}")
    Integer setEmail(String email, int userId);

    @Update("update users set password=#{password} where email=#{email}")
    Integer setPasswordByEmail(String password, String email);

    @Update("update users set password=#{password} ,is_check_email=1,permission_level= if(permission_level>1,permission_level,2) where user_id=#{userId}")
    Integer setPasswordById(String password, Integer userId);

    @Update("update users set gender=#{gender} ,signature=#{signature},location=#{location}  where user_id=#{userId}")
    Integer updateUserInfo(int userId, Integer gender, String signature, String location);

    @Insert("insert into user_collection_summary (user_id) values (#{userId})")
    Integer initSummary(Integer userId);

    @Update("update users set qq_open_id=null where user_id= #{userId}")
    Integer unbindQQ(int userId);

    @Insert("insert into user_upload_image_log (user_id,image_uuid,module_name) values (#{userId},#{uuid},#{moduleName})")
    Integer uploadModuleImageLog(Integer userId, String uuid, String moduleName);

    @Select({
            "SELECT * FROM users WHERE email= #{emailAddr} ",
    })
    @Results({
            @Result(property = "id", column = "user_id"),
            @Result(property = "isBan", column = "is_ban"),
            @Result(property = "permissionLevel", column = "permission_level"),
            @Result(property = "pixivAccount", column = "pixiv_account"),
            @Result(property = "pixivPassword", column = "pixiv_password"),
            @Result(property = "qqOpenId", column = "qq_open_id"),
            @Result(property = "isCheckEmail", column = "is_check_email"),
            @Result(property = "createDate", column = "create_date", typeHandler = org.apache.ibatis.type.LocalDateTimeTypeHandler.class),
            @Result(property = "updateDate", column = "update_date", typeHandler = org.apache.ibatis.type.LocalDateTimeTypeHandler.class),
            @Result(property = "permissionLevelExpireDate", column = "permission_level_expire_date", typeHandler = org.apache.ibatis.type.LocalDateTimeTypeHandler.class)
    })
    User queryUserByEmail(String emailAddr);

    @Update("update users set permission_level_expire_date = date_add(permission_level_expire_date, interval #{type} day) where user_id=#{userId}")
    void extendPermissionLevelExpirationTime(Integer userId, byte type);

    @Update("update users set permission_level_expire_date = #{plusDays,typeHandler=org.apache.ibatis.type.LocalDateTimeTypeHandler},permission_level=#{permissionLevel} where user_id=#{userId}")
    void updatePermissionLevelExpirationTime(Integer userId, int permissionLevel, LocalDateTime plusDays);

    @Update("update users set permission_level=2 where permission_level=3 and permission_level_expire_date<=now()")
    void refreshUserPermissionLevel();

    @Update("update users set star=star+#{star} where user_id=#{userId}")
    int modifyUserPoint(Integer userId, int star);

}
