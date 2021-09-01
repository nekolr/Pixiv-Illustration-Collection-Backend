package dev.cheerfun.pixivic.biz.recommend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RecommendTaskService {
    private final NewIllustBookmarkRecommendService newIllustBookmarkRecommendService;
    private final NewArtistRecommendService newArtistRecommendService;
    private final CacheManager cacheManager;

    @Scheduled(cron = "0 0 2 * * MON,WEB,FRI")
    public void genarateTask() throws TasteException {
        log.info("开始拉取推荐");
        clearCache();
        newIllustBookmarkRecommendService.recommend();
        newArtistRecommendService.recommend();
        //TODO 每1小时新用户推荐拉取
        log.info("拉取推荐结束");
    }

    private void clearCache() {
        //清理缓存
        cacheManager.getCacheNames()
                .forEach(cacheName -> Objects.requireNonNull(cacheManager.getCache(cacheName)).clear());
        System.gc();
    }

}
