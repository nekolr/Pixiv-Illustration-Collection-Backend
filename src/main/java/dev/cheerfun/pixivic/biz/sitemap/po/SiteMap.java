package dev.cheerfun.pixivic.biz.sitemap.po;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/6/28 4:26 下午
 * @description SiteMap
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@XStreamAlias("sitemap")
public class SiteMap {
    private String loc;
    private String lastmod;
}
