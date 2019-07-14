package dev.cheerfun.pixivic.infrastructure.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

/**
 * @author echo huang
 * @version 1.0
 * @date 2019-07-13 11:26
 * @description 基于guava的缓存工具类
 */
public class CacheUtils {
    /**
     * 设置最大存储,默认500
     */
    private static final long MAXIMUM_SIZE = 500;
    /**
     * 设置过期时间,默认30分钟
     */
    private static final int EXPIRE_TIME = 30;
    /**
     * 1.愿意消耗一些内存空间来提升速度。
     * <p>
     * 2.预料到某些键会被多次查询。
     * <p>
     * 3.缓存中存放的数据总量不会超出内存容量。
     */
    private static final Cache<String, String> CACHE_BUILDER =
            CacheBuilder.newBuilder().
                    maximumSize(MAXIMUM_SIZE)
                    .expireAfterWrite(EXPIRE_TIME, TimeUnit.MINUTES)
                    .build();

    public static void put(String key, String value) {
        CACHE_BUILDER.put(key, value);
    }

    public static String get(String key) {
        return CACHE_BUILDER.getIfPresent(key);
    }


}
