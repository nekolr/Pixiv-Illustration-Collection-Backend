package dev.cheerfun.pixivic.biz.sitemap.po;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/6/28 4:27 下午
 * @description Url
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@XStreamAlias("url")
public class Url {
    private String loc;
    private String lastmod;
    private String changefreq;
    private String priority;
}
