package dev.cheerfun.pixivic.common.domain;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2021/9/30 4:16 下午
 * @description CacheRedisProperty
 */
@ConfigurationProperties(prefix = "spring.redis.cache")
@Data
public class CacheRedisProperty {
    private String host;
    private int port;
    private int database;
    private String password;
}
