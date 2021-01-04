package dev.cheerfun.pixivic.biz.web.oauth2.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2021/1/4 4:14 PM
 * @description OAuth2Config
 */
@Configuration
@EnableConfigurationProperties(OauthAuthorizationServer.class)
public class OAuth2Config {
}
