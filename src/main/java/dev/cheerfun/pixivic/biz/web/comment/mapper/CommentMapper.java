package dev.cheerfun.pixivic.biz.web.comment.mapper;

import dev.cheerfun.pixivic.biz.web.comment.po.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface CommentMapper {
    @Insert("insert into comments (app_type, app_id,parent_id,reply_from,reply_from_name,reply_to,reply_to_name,content,create_date,liked_count,platform) " +
            "values (#{appType}, #{appId}, #{parentId}, #{replyFrom}, #{replyFromName},#{replyTo},#{replyToName}, #{content}, #{createDate,typeHandler=org.apache.ibatis.type.LocalDateTimeTypeHandler}, #{likedCount},#{platform})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "comment_id")
    int pushComment(Comment comment);

    @Select("select * from comments where app_type = #{appType} and app_id = #{appId}")
    @Results({
            @Result(property = "id", column = "comment_id"),
            @Result(property = "appType", column = "app_type"),
            @Result(property = "appId", column = "app_id"),
            @Result(property = "parentId", column = "parent_id"),
            @Result(property = "replyTo", column = "reply_to"),
            @Result(property = "replyToName", column = "reply_to_name"),
            @Result(property = "replyFrom", column = "reply_from"),
            @Result(property = "replyFromName", column = "reply_from_name"),
            @Result(property = "createDate", column = "create_Date", typeHandler = org.apache.ibatis.type.LocalDateTimeTypeHandler.class),
            @Result(property = "likedCount", column = "liked_count")
    })
    List<Comment> pullComment(String appType, Integer appId);

    @Update("replace into comment_summary (app_type, app_id, top_comment_count) select #{appType}, #{appId} ,count(*) from comments where app_type= #{appType} and app_id=#{appId} and parent_id=0")
    Integer countCommentSummary(String appType, String appId);

    @Select("select top_comment_count from comment_summary where app_type =#{appType} and app_id=#{appId}")
    Integer queryTopCommentCount(String appType, Integer appId);

    @Select("select * from comments where comment_id = #{commentId} ")
    @Results({
            @Result(property = "id", column = "comment_id"),
            @Result(property = "appType", column = "app_type"),
            @Result(property = "appId", column = "app_id"),
            @Result(property = "parentId", column = "parent_id"),
            @Result(property = "replyTo", column = "reply_to"),
            @Result(property = "replyToName", column = "reply_to_name"),
            @Result(property = "replyFrom", column = "reply_from"),
            @Result(property = "replyFromName", column = "reply_from_name"),
            @Result(property = "createDate", column = "create_Date", typeHandler = org.apache.ibatis.type.LocalDateTimeTypeHandler.class),
            @Result(property = "likedCount", column = "liked_count")
    })
    Comment queryCommentById(Integer commentId);
}
