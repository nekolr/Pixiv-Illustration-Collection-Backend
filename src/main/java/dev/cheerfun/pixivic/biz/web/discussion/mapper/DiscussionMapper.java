package dev.cheerfun.pixivic.biz.web.discussion.mapper;

import dev.cheerfun.pixivic.biz.web.discussion.po.Discussion;
import dev.cheerfun.pixivic.biz.web.discussion.po.Section;
import dev.cheerfun.pixivic.common.util.json.JsonTypeHandler;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

//@Mapper
public interface DiscussionMapper {
    @Insert("insert into discussions (section_id,title,content,user_id,username,tag_list,create_time) values (#{sectionId},#{title},#{content},#{userId},#{username},#{tagList,typeHandler=dev.cheerfun.pixivic.common.util.json.JsonTypeHandler},#{createTime,typeHandler=org.apache.ibatis.type.LocalDateTimeTypeHandler})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "discussion_id")
    Integer createDiscussion(Discussion discussion);

    @Select("select * from discussions where discussion_id=#{discussionId}")
    @Results({
            @Result(property = "id", column = "discussion_id"),
            @Result(property = "sectionId", column = "section_id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "totalUp", column = "total_up"),
            @Result(property = "totalDown", column = "total_down"),
            @Result(property = "totalView", column = "total_view"),
            @Result(property = "createTime", column = "create_time", typeHandler = org.apache.ibatis.type.LocalDateTimeTypeHandler.class),
            @Result(property = "updateTime", column = "update_time", typeHandler = org.apache.ibatis.type.LocalDateTimeTypeHandler.class),
            @Result(property = "tagList", column = "tag_list", javaType = List.class, typeHandler = JsonTypeHandler.class)
    })
    Discussion queryById(Integer discussionId);

    @Insert("insert ignore into user_discussion_option (user_id,discussion_id,`option`) values(#{userId},#{discussionId},#{option})")
    Integer createOption(Integer userId, Integer discussionId, Integer option);

    @Update({
            "<script>",
            "update discussions set",
            "<when test='option&gt;0'>\n",
            "total_up=total_up+1",
            "</when>",
            "<when test='option&lt;0'>\n",
            "total_down=total_down+1",
            "</when>",
            "where discussion_id = #{discussionId}",
            "</script>"
    })
    Integer updateDiscussionTotalUpAndDown(Integer discussionId, Integer option);

    @Update("update discussions set last_reply_time = current_timestamp where discussion_id= #{discussionId}")
    Integer updateSort(Integer discussionId);

    @Select("select discussion_id from discussions where section_id=#{sectorId} order by last_reply_time desc limit #{currIndex},#{pageSize}")
    List<Integer> queryList(Integer sectorId, int currIndex, Integer pageSize);

    @Select("select discussion_count from discussion_section where section_id=#{sectionId}")
    Integer queryListCount(Integer sectionId);

    @Update("update discussion_section set discussion_count =discussion_count +1 where section_id=#{sectionId}")
    Integer decrSectionDiscussionCount(Integer sectionId);

    @Delete("delete from discussions where user_id=#{userId} and discussion_id=#{discussionId}")
    Integer deleteDiscussion(Integer userId, Integer discussionId);

    @Update("update discussions set section_id = #{discussion.sectionId},title=#{discussion.title},content=#{discussion.content},tag_list=#{discussion.tagList,typeHandler=dev.cheerfun.pixivic.common.util.json.JsonTypeHandler} where discussion_id= #{discussion.id} and user_id=#{discussion.userId} and create_time >= #{localDateTime,typeHandler=org.apache.ibatis.type.LocalDateTimeTypeHandler}")
    Integer updateDiscussion(@Param("discussion") Discussion discussion, LocalDateTime localDateTime);

    @Select("select * from discussion_section where use_flag=1")
    @Results({
            @Result(property = "id", column = "section_id"),
            @Result(property = "discussionCount", column = "discussion_count"),
            @Result(property = "createTime", column = "create_time", typeHandler = org.apache.ibatis.type.LocalDateTimeTypeHandler.class),
    })
    List<Section> querySectionList();

    @Insert("select `option` from  user_discussion_option where user_id=#{userId} and discussion_id= #{discussionId}")
    Integer queryOption(Integer userId, Integer discussionId);

}
