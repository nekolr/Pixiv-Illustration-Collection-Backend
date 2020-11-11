package dev.cheerfun.pixivic.biz.recommend.service;

import lombok.RequiredArgsConstructor;
import org.apache.mahout.cf.taste.common.TasteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/7/13 10:05 上午
 * @description RecommendDailyTaskService
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RecommendDailyTaskService {
    private final RecommendService illustBookmarkRecommendService;
    private final RecommendService illustViewRecommendService;
    private final RecommendService artistRecommendService;
    private final CacheManager cacheManager;

    @Scheduled(cron = "0 0 1 * * ?")
    public void genarateTask() throws TasteException {
        clearCache();
        illustBookmarkRecommendService.recommend();
        artistRecommendService.recommend();
        //illustViewRecommendService.recommend();
    }

    private void clearCache() {
        //清理缓存
        cacheManager.getCacheNames()
                .forEach(cacheName -> Objects.requireNonNull(cacheManager.getCache(cacheName)).clear());
        System.gc();
    }

}
