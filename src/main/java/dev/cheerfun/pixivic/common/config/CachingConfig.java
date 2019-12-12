package dev.cheerfun.pixivic.common.config;

import org.springframework.context.annotation.Configuration;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/12/12 14:34
 * @description CachingConfig
 */
@Configuration
public class CachingConfig {
 /*   @Bean
    public CacheManager caffeineCacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        List<CaffeineCache> caffeineCaches = new ArrayList<>();
            caffeineCaches.add(new CaffeineCache("pixivic",
                    Caffeine.newBuilder()
                            .expireAfterWrite(Duration.ofMinutes(10))
                            .maximumSize(500)
                            .build()));
        cacheManager.setCaches(caffeineCaches);
        return cacheManager;
    }*/
}