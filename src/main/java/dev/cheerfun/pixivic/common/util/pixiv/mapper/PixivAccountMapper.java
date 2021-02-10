package dev.cheerfun.pixivic.common.util.pixiv.mapper;

import dev.cheerfun.pixivic.biz.crawler.pixiv.domain.PixivUser;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PixivAccountMapper {
    @Select("select * from pixiv_accounts")
    @Results({
            @Result(property = "id", column = "pixiv_account_id"),
            @Result(property = "username", column = "username"),
            @Result(property = "refreshToken", column = "refresh_token")
    })
    List<PixivUser> queryPixivUser();

    @Update("update pixiv_accounts set refresh_token = #{refreshToken} where pixiv_account_id =#{id}")
    void saveRefreshTokenToDb(Integer id, String refreshToken);
}
