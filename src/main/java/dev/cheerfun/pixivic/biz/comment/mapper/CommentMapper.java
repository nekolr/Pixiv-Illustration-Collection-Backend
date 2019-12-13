package dev.cheerfun.pixivic.biz.comment.mapper;

import dev.cheerfun.pixivic.biz.comment.po.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentMapper {
    @Insert("insert into comments (app_type, app_id,parent_id,from,reply_to,content,create_date) values (#{appType}, #{appId}, #{parentId}, #{from}, #{replyTo}, #{content}, #{createDate})")
    int pushComment(Comment comment);

    @Select("select * from comments where app_type = #{appType} and app_id = #{appId}")
    @Results({
            @Result(property = "id", column = "comment_id"),
            @Result(property = "appType", column = "app_type"),
            @Result(property = "appId", column = "app_id"),
            @Result(property = "parentId", column = "parent_id"),
            @Result(property = "replyTo", column = "reply_To"),
            @Result(property = "createDate", column = "create_Date"),
            @Result(property = "likedCount", column = "liked_count")
    })
    List<Comment> pullComment(String appType,Integer appId);

}
