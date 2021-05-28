package dev.cheerfun.pixivic.biz.web.wx.config;

import dev.cheerfun.pixivic.biz.web.wx.handler.*;
import lombok.AllArgsConstructor;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2021/5/28 3:18 PM
 * @description WXMpConfig
 */
@AllArgsConstructor
//@Configuration
@EnableConfigurationProperties(WxMpProperties.class)
public class WXMpConfig {
    private final LogHandler logHandler;
    private final ImageMsgHandler imageMsgHandler;
    private final WxMpProperties properties;

    @Bean
    public WxMpService wxMpService() {
        WxMpDefaultConfigImpl configStorage;
        WxMpService service = new WxMpServiceImpl();
        configStorage = new WxMpDefaultConfigImpl();
        configStorage.setAppId(properties.getAppId());
        configStorage.setSecret(properties.getSecret());
        configStorage.setToken(properties.getToken());
        configStorage.setAesKey(properties.getAesKey());
        service.setWxMpConfigStorage(configStorage);
        return service;
    }

    @Bean
    public WxMpMessageRouter messageRouter(WxMpService wxMpService) {
        final WxMpMessageRouter newRouter = new WxMpMessageRouter(wxMpService);
        // 图片
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.IMAGE).handler(this.imageMsgHandler).end();
        return newRouter;
    }

}
