package dev.cheerfun.pixivic.biz.web.discover.mapper;

import dev.cheerfun.pixivic.biz.web.discover.po.DiscoverNode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

//@Mapper
public interface DiscoverMapper {
    @Select("select * from discover_nodes where use_flag=1 order by order_col")
    @Results({
            @Result(column = "discover_node_id", property = "id"),
            @Result(column = "order_col", property = "orderCol"),
            @Result(column = "use_flag", property = "useFlag")
    })
    List<DiscoverNode> queryAllNode();
}
