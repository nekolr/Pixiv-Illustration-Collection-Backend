package dev.cheerfun.pixivic.biz.web.discover.po;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/2/24 下午9:32
 * @description Dir
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Dir extends DiscoverNode {
    protected List<DiscoverNode> apps;

    public Dir(DiscoverNode discoverNode, List<DiscoverNode> apps) {
        id = discoverNode.id;
        pid = discoverNode.pid;
        title = discoverNode.title;
        type = discoverNode.type;
        icon = discoverNode.icon;
        link = discoverNode.link;
        orderCol = discoverNode.orderCol;
        useFlag = discoverNode.useFlag;
        this.apps = apps;
    }
}
