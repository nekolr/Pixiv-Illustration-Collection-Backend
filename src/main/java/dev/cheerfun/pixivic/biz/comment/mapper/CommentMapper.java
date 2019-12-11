package dev.cheerfun.pixivic.biz.comment.mapper;

import dev.cheerfun.pixivic.biz.comment.po.Comment;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper {
    @Insert("insert into comments (app_type, app_id,parent_id,from,reply_to,content,create_date) values (#{appType}, #{appId}, #{parentId}, #{from}, #{replyTo}, #{content}, #{createDate})")
    int pushComment(Comment comment);

}
