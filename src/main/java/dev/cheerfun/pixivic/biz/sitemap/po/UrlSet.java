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
 * @description UrlSet
 */
@Data
@XStreamAlias("urlset")
public class UrlSet {
    @XStreamAlias("xmlns")
    @XStreamAsAttribute
    private String xmlns = SiteMapConstant.XMLNS;
    @XStreamAlias("xmlns:xsi")
    @XStreamAsAttribute
    private String xmlns_xsi = SiteMapConstant.XMLNS_XSI;
    @XStreamAlias("xsi:schemaLocation")
    @XStreamAsAttribute
    private String xsi_schemalocation = SiteMapConstant.XSI_SCHEMALOCATION;
    @XStreamImplicit(itemFieldName = "url")
    private List<Url> urlList;

    public UrlSet(List<Url> urlList) {
        this.urlList = urlList;
    }

    public UrlSet() {
    }
}
