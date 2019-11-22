package dev.cheerfun.pixivic.biz.web.user.mapper;

import dev.cheerfun.pixivic.biz.web.user.model.Announcement;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AnnouncementMapper {
    @Insert("insert into announcements (title, content,date,is_Pop) values (#{title}, #{content}, #{date}, #{isPop})")
    int insert(Announcement announcement);

    @Select("select * from announcements where date = #{date}")
    List<Announcement> query(String date);
}
