package dev.cheerfun.pixivic.basic.auth.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author echo huang
 * @version 1.0
 * @date 2019-07-16 22:50
 * @description
 */
@Configuration
@EnableConfigurationProperties(AuthProperties.class)
public class AuthConfig {
}
