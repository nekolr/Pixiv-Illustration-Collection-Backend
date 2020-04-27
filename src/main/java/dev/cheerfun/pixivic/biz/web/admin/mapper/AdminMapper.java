package dev.cheerfun.pixivic.biz.web.admin.mapper;

import dev.cheerfun.pixivic.biz.web.admin.dto.IllustDTO;
import dev.cheerfun.pixivic.biz.web.admin.dto.UsersDTO;
import dev.cheerfun.pixivic.biz.web.common.po.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface AdminMapper {
    @Select({
            "<script>",
            "SELECT * FROM users ",
            "<if test=\"userDTO.id!=null\">\n",
            "AND user_id=#{userDTO.id}\n",
            "</if>",
            "<if test=\"userDTO.username!=null\">\n",
            "AND username=#{userDTO.username}\n",
            "</if>",
            "<if test=\"userDTO.email!=null\">\n",
            "AND email=#{userDTO.email}\n",
            "</if>",
            "order by user_id desc limit #{currIndex},#{pageSize}",
            "</script>"
    })
    List<User> queryUsers(UsersDTO usersDTO, Integer currIndex, Integer pageSize);

    @Select({
            "<script>",
            "SELECT count(*) FROM users ",
            "<if test=\"userDTO.id!=null\">\n",
            "AND user_id=#{userDTO.id}\n",
            "</if>",
            "<if test=\"userDTO.username!=null\">\n",
            "AND username=#{userDTO.username}\n",
            "</if>",
            "<if test=\"userDTO.email!=null\">\n",
            "AND email=#{userDTO.email}\n",
            "</if>",
            "order by user_id desc limit #{currIndex},#{pageSize}",
            "</script>"
    })
    Integer queryUsersTotal(UsersDTO usersDTO, Integer currIndex, Integer pageSize);

    @Update({
            "<script>",
            "update users set",
            "</if>",
            "<if test=\"userDTO.username!=null\">\n",
            "username=#{userDTO.username}\n",
            "</if>",
            "<if test=\"userDTO.email!=null\">\n",
            ",email=#{userDTO.email}\n",
            "</if>",
            "<if test=\"userDTO.isBan!=null\">\n",
            ",is_ban=#{userDTO.isBan}\n",
            "</if>",
            "where user_id=#{userDTO.id}",
            "</script>"
    })
    void updateUser(UsersDTO usersDTO);

    @Update({
            "<script>",
            "update illusts set",
            "</if>",
            "<if test=\"illustDTO.xRestrict!=null\">\n",
            "x_restrict=#{illustDTO.xRestrict}\n",
            "</if>",
            "<if test=\"illustDTO.sanityLevel!=null\">\n",
            ",sanity_level=#{illustDTO.sanityLevel}\n",
            "</if>",
            "where illust_id=#{illustDTO.id}",
            "</script>"
    })
    void updateIllusts(IllustDTO illustDTO);
}
