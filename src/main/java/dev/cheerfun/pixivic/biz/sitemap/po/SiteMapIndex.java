package dev.cheerfun.pixivic.biz.sitemap.po;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import dev.cheerfun.pixivic.biz.sitemap.constant.SiteMapConstant;
import lombok.Data;

import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/6/28 4:27 下午
 * @description SiteMapIndex
 */
@Data
@XStreamAlias("sitemapindex")
public class SiteMapIndex {
    @XStreamAlias("xmlns")
    @XStreamAsAttribute
    private String xmlns = SiteMapConstant.XMLNS;
    @XStreamImplicit(itemFieldName = "sitemap")
    private List<SiteMap> siteMapList;
}
