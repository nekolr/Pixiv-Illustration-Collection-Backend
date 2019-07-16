package dev.cheerfun.pixivic.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author echo huang
 * @version 1.0
 * @date 2019-07-16 22:48
 * @description
 */
@ConfigurationProperties(prefix = "jjwt")
@Data
public class AuthProperties {
    private String secret;

    private String expirationTime;

    private long refreshInterval;
}
