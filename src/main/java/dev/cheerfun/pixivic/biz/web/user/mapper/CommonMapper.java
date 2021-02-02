package dev.cheerfun.pixivic.biz.web.user.mapper;

import dev.cheerfun.pixivic.biz.web.common.po.User;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;

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

    @Select({
            "SELECT IFNULL((\n" +
                    "SELECT 1\n" +
                    "FROM users\n" +
                    "WHERE phone=#{phone} \n" +
                    "LIMIT 1),0)",
    })
    int checkUserPhone(String phone);

    @Insert("insert into users (email,phone, username,password,permission_level,is_ban,star,create_date) values (#{email}, #{phone}, #{username}, #{password}, #{permissionLevel}, #{isBan},#{star},#{createDate,typeHandler=org.apache.ibatis.type.LocalDateTimeTypeHandler})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "user_id")
    Integer insertUser(User user);

    @Select({
            " SELECT user_id FROM (SELECT * FROM users WHERE username= #{username} OR email=#{username})  temp where temp.PASSWORD=#{password}",
    })
    Integer queryUserByusernameAndPassword(String username, String password);

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
            @Result(property = "permissionLevelExpireDate", column = "permission_level_expire_date", typeHandler = org.apache.ibatis.type.LocalDateTimeTypeHandler.class),
            @Result(property = "idCard", column = "id_card"),
            @Result(property = "addrForVerify", column = "addr_for_verify"),
            @Result(property = "birthdayForVerify", column = "birthday_for_verify"),
            @Result(property = "ageForVerify", column = "age_for_verify"),

    })
    User queryUserByUserId(int userId);

    @Select("SELECT user_id FROM users WHERE qq_open_id=#{qqOpenId}\n")
    Integer getUserByQQOpenId(String qqOpenId);

    @Update("update users set qq_open_id=#{qqOpenId} where user_id=#{userId}")
    Integer setQQOpenId(String qqOpenId, int userId);

    @Update("update users set avatar=#{avatar} where user_id=#{userId}")
    Integer setAvatar(String avatar, int userId);

    @Update("update users set email=#{email} , is_check_email=1 where user_id=#{userId}")
    Integer setEmail(String email, int userId);

    @Update("update users set phone=#{phone} ,permission_level= if(permission_level>1,permission_level,2) where user_id=#{userId}")
    Integer setPhone(String phone, int userId);

    @Update("update users set password=#{password} where email=#{email}")
    Integer setPasswordByEmail(String password, String email);

    @Update("update users set password=#{password} ,is_check_email=1 where user_id=#{userId}")
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
            "SELECT user_id FROM users WHERE email= #{emailAddr} ",
    })
    Integer queryUserByEmail(String emailAddr);

    @Update("update users set permission_level_expire_date = date_add(permission_level_expire_date, interval #{type} day) where user_id=#{userId}")
    void extendPermissionLevelExpirationTime(Integer userId, byte type);

    @Update("update users set permission_level_expire_date = #{plusDays,typeHandler=org.apache.ibatis.type.LocalDateTimeTypeHandler},permission_level=#{permissionLevel} where user_id=#{userId}")
    void updatePermissionLevelExpirationTime(Integer userId, int permissionLevel, LocalDateTime plusDays);

    @Update("update users set permission_level=if(phone is null,1, if(permission_level>3,permission_level,2)) where permission_level=3 and permission_level_expire_date<=now()")
    void refreshUserPermissionLevel();

    @Update("update users set star=star+#{star} where user_id=#{userId}")
    int modifyUserPoint(Integer userId, int star);

    @Select("select check_in_date from user_check_in_log where user_id=#{userId} order by check_in_date desc limit 1 ")
    String queryRecentCheckDate(Integer userId);

    @Insert("insert into user_check_in_log(user_id,check_in_date) values (#{userId},#{date})")
    void checkIn(Integer userId, String date);

    @Select("select create_time from user_update_username_log where user_id=#{userId} order by create_time desc limit 1")
    @Results({
            @Result(javaType = LocalDateTime.class, column = "create_time", typeHandler = org.apache.ibatis.type.LocalDateTimeTypeHandler.class)
    })
    LocalDateTime queryUserUpdateUsernameLog(Integer userId);

    @Update("update users set username=#{username} where user_id=#{userId}")
    void updateUsername(Integer userId, String username);

    @Update("update comments set reply_from_name = #{username} where reply_from=#{userId}")
    void updateFromUsernameInComments(Integer userId, String username);

    @Update("update comments set reply_to_name = #{username} where reply_to=#{userId}")
    void updateToUsernameInComments(Integer userId, String username);

    @Update("update discussions set username = #{username} where user_id=#{userId}")
    void updateUsernameInDiscussions(Integer userId, String username);

    @Update("update collections set username = #{username} where user_id=#{userId}")
    void updateUsernameInCollections(Integer userId, String username);

    @Update("update rewards set reward_from_username = #{username} where reward_from=#{userId}")
    void updateUsernameInRewards(Integer userId, String username);

    @Update("update user_artist_followed set username = #{username} where user_id=#{userId}")
    void updateUsernameInUserArtistFollowed(Integer userId, String username);

    @Update("update user_illust_bookmarked set username = #{username} where user_id=#{userId}")
    void updateUsernameInUserIllustBookmarked(Integer userId, String username);

    @Update("update user_collection_bookmarked set username = #{username} where user_id=#{userId}")
    void updateUsernameInUserCollectionBookmarked(Integer userId, String username);

    @Update("update user_user_followed set username = #{username} where user_id=#{userId}")
    void updateUsernameInUserUserFollowed(Integer userId, String username);

    @Insert("insert into user_update_username_log (user_id,create_time) values (#{userId},now())")
    void insertUserUpdateUsernameLog(Integer userId);

    @Update("update users set id_card = #{idCard},addr_for_verify=#{address},birthday_for_verify=#{birthDate},age_for_verify=#{age} where user_id=#{userId}")
    void updateUserVerifiedInfo(int userId, String idCard, String birthDate, int age, String address);

    @Select("select user_id from users where phone=#{phone}")
    Integer queryUserByPhone(String phone);
}
