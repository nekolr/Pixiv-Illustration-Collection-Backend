package dev.cheerfun.pixivic.biz.proxy.mapper;

import dev.cheerfun.pixivic.biz.proxy.po.VIPProxyServer;
import dev.cheerfun.pixivic.common.util.json.JsonTypeHandler;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/10/12 2:36 PM
 * @description VIPProxyServerMapper
 */

public interface VIPProxyServerMapper {
    @Update("update vip_proxy_servers set use_flag=0 where server_id=#{serverId}")
    void ban(Integer serverId);

    @Select("select * from vip_proxy_servers where use_flag=1")
    @Results({
            @Result(property = "id", column = "server_id"),
            @Result(property = "serverAddress", column = "server_address"),
            @Result(property = "useFlag", column = "use_flag"),
            @Result(property = "createDate", column = "create_date", typeHandler = org.apache.ibatis.type.LocalDateTimeTypeHandler.class)
    })
    List<VIPProxyServer> queryAllServer();

    @Insert("insert into vip_proxy_servers (server_address,create_date) values(#{serverAddress},now())")
    void addServer(String vipProxyServer);
}
