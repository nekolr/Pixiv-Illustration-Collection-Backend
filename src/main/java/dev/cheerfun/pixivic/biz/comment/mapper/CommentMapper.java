package dev.cheerfun.pixivic.biz.comment.mapper;

import dev.cheerfun.pixivic.biz.comment.po.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentMapper {
    @Insert("insert into comments (app_type, app_id,parent_id,reply_from,reply_to,content,create_date,liked_count) " +
            "values (#{appType}, #{appId}, #{parentId}, #{replyFrom}, #{replyTo}, #{content}, #{createDate,typeHandler=org.apache.ibatis.type.LocalDateTimeTypeHandler}, #{likedCount})")
    int pushComment(Comment comment);

    @Select("select * from comments where app_type = #{appType} and app_id = #{appId}")
    @Results({
            @Result(property = "id", column = "comment_id"),
            @Result(property = "appType", column = "app_type"),
            @Result(property = "appId", column = "app_id"),
            @Result(property = "parentId", column = "parent_id"),
            @Result(property = "replyTo", column = "reply_to"),
            @Result(property = "replyFrom", column = "reply_from"),
            @Result(property = "createDate", column = "create_Date",typeHandler =org.apache.ibatis.type.LocalDateTimeTypeHandler.class),
            @Result(property = "likedCount", column = "liked_count")
    })
    List<Comment> pullComment(String appType,Integer appId);

}
