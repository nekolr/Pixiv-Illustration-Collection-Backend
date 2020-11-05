package dev.cheerfun.pixivic.biz.web.vip.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

//@Mapper
public interface ExchangeCodeMapper {
    @Insert("insert into vip_exchange_codes(code_id, type, create_date) values (#{sn},#{type},#{now})")
    void inertExchangeCode(int sn, byte type, String now);

    @Update("update vip_exchange_codes set use_flag=1 where code_id =#{codeId}")
    void updateExchangeCode(int codeId);
}
