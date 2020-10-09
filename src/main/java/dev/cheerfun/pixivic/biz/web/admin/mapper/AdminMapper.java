package dev.cheerfun.pixivic.biz.web.admin.mapper;

import dev.cheerfun.pixivic.biz.web.admin.dto.IllustDTO;
import dev.cheerfun.pixivic.biz.web.admin.dto.UsersDTO;
import dev.cheerfun.pixivic.biz.web.comment.po.Comment;
import dev.cheerfun.pixivic.biz.web.common.po.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AdminMapper {

    @Select({
            "<script>",
            "SELECT * FROM users WHERE 1=1",
            "<when test='userDTO.id!=null'>\n",
            "AND user_id=#{userDTO.id}\n",
            "</when>",
            "<when test='userDTO.username!=null'>\n",
            "AND username=#{userDTO.username}\n",
            "</when>",
            "<when test='userDTO.email!=null'>\n",
            "AND email=#{userDTO.email}\n",
            "</when>",
            "order by ${orderBy} ${orderByMode} limit #{currIndex},#{pageSize}",
            "</script>"
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
            @Result(property = "updateDate", column = "update_date", typeHandler = org.apache.ibatis.type.LocalDateTimeTypeHandler.class)

    })
    List<User> queryUsers(UsersDTO userDTO, Integer currIndex, Integer pageSize, String orderBy, String orderByMode);

    @Select({
            "<script>",
            "SELECT count(*) FROM users WHERE 1=1",
            "<when test='userDTO.id!=null'>\n",
            "AND user_id=#{userDTO.id}\n",
            "</when>",
            "<when test='userDTO.username!=null'>\n",
            "AND username=#{userDTO.username}\n",
            "</when>",
            "<when test='userDTO.email!=null'>\n",
            "AND email=#{userDTO.email}\n",
            "</when>",
            "</script>"
    })
    Integer queryUsersTotal(UsersDTO userDTO, Integer currIndex, Integer pageSize);

    @Update({
            "<script>",
            "update users set",
            "<when test='userDTO.username!=null'>\n",
            "username=#{userDTO.username}\n",
            "</when>",
            "<when test='userDTO.email!=null'>\n",
            ",email=#{userDTO.email}\n",
            "</when>",
            "<when test='userDTO.isBan!=null'>\n",
            ",is_ban=#{userDTO.isBan}\n",
            "</when>",
            "where user_id=#{userDTO.id}",
            "</script>"
    })
    void updateUser(UsersDTO usersDTO);

    @Update("update users set is_ban=0 where user_id=#{userId}")
    Integer banUser(Integer userId);

    @Update({
            "<script>",
            "update illusts set",
            "<when test='illustDTO.xRestrict!=null'>\n",
            "x_restrict=#{illustDTO.xRestrict}\n",
            "</when>",
            "<when test='illustDTO.sanityLevel!=null'>\n",
            ",sanity_level=#{illustDTO.sanityLevel}\n",
            "</when>",
            "where illust_id=#{illustDTO.id}",
            "</script>"
    })
    void updateIllusts(IllustDTO illustDTO);

    @Select("select token from admin_key where use_flag=1")
    List<String> queryAllAdminKey();

    @Select({
            "<script>",
            "SELECT * FROM comments WHERE 1=1",
            "<when test='comment.id!=null'>\n",
            "AND comment_id=#{comment.id}\n",
            "</when>",
            "<when test='comment.appType!=null'>\n",
            "AND app_type=#{comment.appType}\n",
            "</when>",
            "<when test='comment.appId!=null'>\n",
            "AND app_id=#{comment.appId}\n",
            "</when>",
            "<when test='comment.replyFrom!=null'>\n",
            "AND reply_from=#{comment.replyFrom}\n",
            "</when>",
            "<when test='comment.replyFromName!=null'>\n",
            "AND reply_from_name=#{comment.replyFromName}\n",
            "</when>",
            "order by #{orderBy} #{orderByMode} limit #{currIndex},#{pageSize}",
            "</script>"
    })
    List<Comment> queryComment(Comment comment, Integer page, Integer pageSize, String orderBy, String orderByMode);

    @Select({
            "<script>",
            "SELECT count(*) FROM comments WHERE 1=1",
            "<when test='comment.id!=null'>\n",
            "AND comment_id=#{comment.id}\n",
            "</when>",
            "<when test='comment.appType!=null'>\n",
            "AND app_type=#{comment.appType}\n",
            "</when>",
            "<when test='comment.appId!=null'>\n",
            "AND app_id=#{comment.appId}\n",
            "</when>",
            "<when test='comment.replyFrom!=null'>\n",
            "AND reply_from=#{comment.replyFrom}\n",
            "</when>",
            "<when test='comment.replyFromName!=null'>\n",
            "AND reply_from_name=#{comment.replyFromName}\n",
            "</when>",
            "",
            "</script>"
    })
    Integer queryCommentTotal(Comment comment, Integer page, Integer pageSize);

    @Insert("insert ignore into block_illust_set (illust_id,create_date) values (#{illustId},now())")
    Integer blockIllustrationById(Integer illustId);

    @Select("select illust_id from block_illust_set")
    List<Integer> queryBlockIllust();

    @Select("select illust_id from block_illust_set where illust_id =#{illustId}")
    List<Integer> queryBlockIllustById(Integer illustId);

    @Delete("delete from block_illust_set where illust_id =#{illustId}")
    Integer removeIllustFromBlockIllust(Integer illustId);

    @Insert("insert into block_artist_set (artist_id,create_date) values (#{artistId},now())")
    int blockArtistById(Integer artistId);

    @Select("select illust_id from illusts where artist_id = #{artistId}")
    List<Integer> queryIllustrationsByArtistId(Integer artistId);

    @Select("select artist_id from block_artist_set where artist_id=#{artistId}")
    List<Integer> queryBlockArtistById(Integer artistId);

    @Select("select artist_id from block_artist_set")
    List<Integer> queryBlockArtist();

    @Delete("delete from block_artist_set where artist_id =#{artistId}")
    int removeArtistFromBlockArtist(Integer artistId);
}
