package dev.cheerfun.pixivic.biz.web.announcement.mapper;

import dev.cheerfun.pixivic.biz.web.announcement.po.Announcement;
import org.apache.ibatis.annotations.*;

import java.util.List;

//@Mapper
public interface AnnouncementMapper {
    @Insert("insert into announcements (title, content,date,is_Pop) values (#{title}, #{content}, #{date}, #{isPop})")
    int insert(Announcement announcement);

    @Select("select announcement_id from announcements where start_date <= #{date} and end_date>=#{date}")
    List<Integer> queryByDate(String date);

    @Select("select announcement_id from announcements limit #{currIndex},#{pageSize}")
    List<Integer> queryList(int currIndex, Integer pageSize);

    @Select("select * from announcements where announcement_id = #{id} ")
    @Results({
            @Result(property = "id", column = "announcement_id"),
            @Result(property = "startDate", column = "start_date", typeHandler = org.apache.ibatis.type.LocalDateTimeTypeHandler.class),
            @Result(property = "endDate", column = "end_date", typeHandler = org.apache.ibatis.type.LocalDateTimeTypeHandler.class),
            @Result(property = "createDate", column = "create_date", typeHandler = org.apache.ibatis.type.LocalDateTimeTypeHandler.class),
    })
    Announcement queryById(Integer id);

    @Select("select count(*) from announcements")
    Integer queryListCount();

}
