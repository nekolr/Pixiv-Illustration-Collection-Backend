package dev.cheerfun.pixivic.biz.web.discover.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/2/24 下午9:36
 * @description DiscoverNode
 */
@Data
public class DiscoverNode {
    protected Integer id;
    protected Integer pid;
    protected String title;
    protected String type;
    protected String icon;
    protected String link;
    @JsonIgnore
    protected String orderCol;
    protected Integer useFlag;

}
