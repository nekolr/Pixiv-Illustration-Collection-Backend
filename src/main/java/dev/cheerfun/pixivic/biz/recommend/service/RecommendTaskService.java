package dev.cheerfun.pixivic.biz.recommend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.mahout.cf.taste.common.TasteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

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
    private final ExecutorService recommendExecutorService;

    @PostConstruct
    public void init() {
        log.info("开始初始化推荐任务服务");
        newUserGenerateTask();
        log.info("初始化推荐服务任务成功");
    }

    public void newUserGenerateTask() {
        //每分钟为新用户拉取600个推荐（缓存一下新用户推荐列表）
        recommendExecutorService.submit(() -> {
            while (true) {
                try {
                    newIllustBookmarkRecommendService.recommendForNewUser();
                    newArtistRecommendService.recommendForNewUser();
                    //十分钟同步一次
                    Thread.sleep(60 * 1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Scheduled(cron = "0 0 2 * * MON,WEB,FRI")
    public void generateTask() throws TasteException {
        log.info("开始拉取推荐");
        clearCache();
        newIllustBookmarkRecommendService.recommend();
        newArtistRecommendService.recommend();
        log.info("拉取推荐结束");
    }

    private void clearCache() {
        //清理缓存
        cacheManager.getCacheNames()
                .forEach(cacheName -> Objects.requireNonNull(cacheManager.getCache(cacheName)).clear());
        System.gc();
    }

}
