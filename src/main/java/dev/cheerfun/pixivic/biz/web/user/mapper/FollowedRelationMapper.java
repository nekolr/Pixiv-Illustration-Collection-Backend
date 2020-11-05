package dev.cheerfun.pixivic.biz.web.user.mapper;

import dev.cheerfun.pixivic.common.po.Artist;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

//@Mapper
public interface FollowedRelationMapper {
    @Insert("insert into user_artist_followed (user_id, artist_id,create_date) values (#{userId}, #{artistId}, #{now,typeHandler=org.apache.ibatis.type.LocalDateTimeTypeHandler})")
    int follow(int userId, int artistId, LocalDateTime now);

    @Delete("delete from user_artist_followed where user_id=#{userId} and artist_id = #{artistId}")
    int cancelFollow(int userId, int artistId);

    @Select("select a.* from (select artist_id from user_artist_followed where user_id = #{userId}) u left join artists a on u.artist_id = a.artist_id limit #{currIndex} , #{pageSize}")
    @Results({
            @Result(property = "id", column = "artist_id"),
    })
    List<Artist> queryFollowed(int userId, int currIndex, int pageSize);

    @Select("select  count(id) from user_artist_followed where user_id = #{userId} and artist_id=#{artistId}")
    int queryIsFollowed(int userId, int artistId);
}
