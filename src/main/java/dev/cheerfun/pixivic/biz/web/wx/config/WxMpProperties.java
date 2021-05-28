package dev.cheerfun.pixivic.biz.web.wx.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2021/5/28 3:18 PM
 * @description WxMpProperties
 */
@Data
//@ConfigurationProperties(prefix = "wx.mp")
public class WxMpProperties {
    /**
     * 设置微信公众号的appid
     */
    private String appId;

    /**
     * 设置微信公众号的app secret
     */
    private String secret;

    /**
     * 设置微信公众号的token
     */
    private String token;

    /**
     * 设置微信公众号的EncodingAESKey
     */
    private String aesKey;
}

