package dev.cheerfun.pixivic.biz.proxy.mapper;

import dev.cheerfun.pixivic.biz.proxy.po.VIPProxyServer;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/10/12 2:36 PM
 * @description VIPProxyServerMapper
 */
@Mapper
public interface VIPProxyServerMapper {
    @Update("update vip_proxy_servers set use_flag=0 where server_id=#{serverId}")
    void ban(Integer serverId);

    @Select("select * from vip_proxy_servers where use_flag=1")
    List<VIPProxyServer> queryAllServer();

    @Insert("insert into vip_proxy_servers (server_address,create_date) values(#{serverAddress},now())")
    void addServer(VIPProxyServer vipProxyServer);
}
