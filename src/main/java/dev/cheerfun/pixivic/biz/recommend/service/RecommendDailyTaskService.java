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
    private final NewIllustBookmarkRecommendService newIllustBookmarkRecommendService;

    @Scheduled(cron = "0 0 1 * * WED")
    public void genarateTask() throws TasteException {
        newIllustBookmarkRecommendService.recommend();
    }


}
