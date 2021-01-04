package dev.cheerfun.pixivic.biz.web.oauth2.mapper;

import dev.cheerfun.pixivic.biz.web.oauth2.po.OAuth2Client;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OAuth2Mapper {
    @Select("select * from oauth2_clients where use_flag=1")
    @Results({
            @Result(property = "clientId", column = "oauth2_client_id"),
            @Result(property = "clientSecret", column = "client_secret"),
            @Result(property = "redirectUri", column = "redirect_uri")
    })
    List<OAuth2Client> queryOAuth2ClientList();
}
