package dev.cheerfun.pixivic.common.config;

import dev.cheerfun.pixivic.basic.auth.config.AuthProperties;
import dev.cheerfun.pixivic.common.domain.CacheRedisProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2021/9/30 4:15 下午
 * @description CacheConfig
 */
@Configuration
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@EnableConfigurationProperties(CacheRedisProperty.class)
public class CacheConfig {

    @Bean(name = "userCacheManager")
    @Primary
    public CacheManager cacheManager(@Autowired CacheRedisProperty cacheRedisProperty) {
        RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration();
        redisConfiguration.setHostName(cacheRedisProperty.getHost());
        redisConfiguration.setPort(cacheRedisProperty.getPort());
        redisConfiguration.setDatabase(cacheRedisProperty.getDatabase());
        redisConfiguration.setPassword(cacheRedisProperty.getPassword());
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(new JedisConnectionFactory(redisConfiguration));
        RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext
                        .SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext
                        .SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
        //.entryTtl(Duration.ofHours(2));
        return new RedisCacheManager(redisCacheWriter, cacheConfiguration);
    }

    /*@Bean(name = "cacheRedisTemplate")
    public RedisTemplate cacheRedisTemplate(@Qualifier("cacheRedisConnectionFactory") RedisConnectionFactory cf) {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate(cf);
        stringRedisTemplate.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
        stringRedisTemplate.setKeySerializer(new StringRedisSerializer());
        stringRedisTemplate.setHashKeySerializer(new GenericJackson2JsonRedisSerializer());
        stringRedisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return stringRedisTemplate;
    }*/

}
